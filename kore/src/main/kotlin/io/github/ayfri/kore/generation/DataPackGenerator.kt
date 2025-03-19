package io.github.ayfri.kore.generation

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.pack.PackMCMeta
import io.github.ayfri.kore.utils.Path
import io.github.ayfri.kore.utils.SystemPathSeparatorString
import io.github.ayfri.kore.utils.TemporaryFiles
import io.github.ayfri.kore.utils.absolute
import io.github.ayfri.kore.utils.asInvariantPathSeparator
import io.github.ayfri.kore.utils.ensureParents
import io.github.ayfri.kore.utils.exists
import io.github.ayfri.kore.utils.isDirectory
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.nameWithoutExtension
import io.github.ayfri.kore.utils.readText
import io.github.ayfri.kore.utils.relativeTo
import io.github.ayfri.kore.utils.resolveSafe
import io.github.ayfri.kore.utils.resolve
import io.github.ayfri.kore.utils.toJavaFile
import io.github.ayfri.kore.utils.toSink
import io.github.ayfri.kore.utils.toSource
import io.github.ayfri.kore.utils.toStringWithSeperator
import io.github.ayfri.kore.utils.warn
import io.github.ayfri.kore.utils.write
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import java.nio.file.FileSystemException
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

internal fun unzip(zipFile: Path): Path {
	val cleanName = zipFile.nameWithoutExtension.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
	val tempDir = TemporaryFiles.createTempDirectory("kore_unzipped_datapack_$cleanName")
	ZipInputStream(zipFile.toSource().buffered().asInputStream()).use { zipInputStream ->
		var entry = zipInputStream.nextEntry
		while (entry != null) {
			val filePath = tempDir.resolveSafe(entry.name)
			if (entry.isDirectory) {
				filePath.makeDirectories()
				filePath
			} else {
				// Ensure parent directories exist
				filePath.ensureParents()
				// Copy the file content
				filePath.toSink().buffered().asOutputStream().use(zipInputStream::copyTo)
			}
			zipInputStream.closeEntry()
			entry = zipInputStream.nextEntry
		}
	}

	return tempDir
}

enum class DatapackGenerationMode {
	FOLDER,
	JAR,
	ZIP,
}

data class DataPackGenerator(
	var datapack: DataPack,
	var options: DataPackGenerationCommonOptions = DataPackGenerationOptions(),
	var mode: DatapackGenerationMode = DatapackGenerationMode.FOLDER,
) {
	val outputPath = when (mode) {
		DatapackGenerationMode.FOLDER -> dataPackPath.resolve(datapack.name)
		DatapackGenerationMode.JAR -> dataPackPath.resolve("${datapack.name}.jar")
		DatapackGenerationMode.ZIP -> dataPackPath.resolve("${datapack.name}.zip")
	}

	val dataPackPath get() = datapack.cleanPath
	val outputDataPath get() = outputPath.resolve("data")

	private val outputStream = when (mode) {
		DatapackGenerationMode.ZIP -> ZipOutputStream(FileOutputStream(outputPath.toJavaFile()))
		DatapackGenerationMode.JAR -> JarOutputStream(FileOutputStream(outputPath.toJavaFile()))
		else -> null
	}

	fun generate() {
		if (datapack.generated) {
			warn("Trying to generate the already generated datapack '${datapack.name}' in $dataPackPath.")
			outputStream?.close()
			return
		}

		val start = System.currentTimeMillis()
		val packMCMeta = datapack.generatePackMCMetaFile()

		init()
		writeFile("pack.mcmeta", packMCMeta)

		datapack.iconPath?.let { writeFile("pack.png", SystemFileSystem.source(it).buffered().readByteArray()) }

		datapack.functions.distinctBy(Function::getFinalPath).forEach {
			val path = it.getFinalPath().replace("\\", "/")
			writeFile(path, it.lines.joinToString("\n"))
		}

		datapack.generatedFunctions.distinctBy(Function::getFinalPath).forEach {
			val path = it.getFinalPath().replace("\\", "/")
			writeFile(path, it.lines.joinToString("\n"))
		}

		datapack.generators.flatten()
			.distinctBy { it.getFinalPath(datapack) }
			.forEach { generator ->
				val namespace = generator.namespace ?: datapack.name
				val path = generator.getPathFromDataDir(outputDataPath, namespace)
				writeFile(path.toString(), generator.generateJson(datapack))
			}

		val end = System.currentTimeMillis()
		println("Generated data pack '${datapack.name}' in ${end - start}ms in: $dataPackPath")

		if (options.mergeWithPacks.isEmpty()) {
			outputStream?.close()
			datapack.generated = true
			return
		}

		val mergeWithPacks = options.mergeWithPacks.sortedBy { it.name.toString() }
		println("Merging datapack with other packs: ${mergeWithPacks.joinToString(", ")}")

		val tagsToMerge = listOf("minecraft/tags/function/load.json", "minecraft/tags/functions/tick.json")
		val foundTags = tagsToMerge.associateWith { mutableListOf<Tag<TaggedResourceLocationArgument>>() }

		mergeWithPacks.forEach { otherPath ->
			require(otherPath.exists()) { "The pack at '$otherPath' does not exist." }

			var otherPackFile = otherPath
			if (otherPath.toString().endsWith(".zip")) {
				otherPackFile = unzip(otherPath)
				println("Unzipped pack '$otherPath' to: ${otherPackFile.absolute()}")
			}

			if (otherPackFile == dataPackPath) {
				println("Skipping merging with the current datapack.")
				return@forEach
			}

			val otherPackMCMetaFile = otherPackFile.resolve("pack.mcmeta")
			require(otherPackMCMetaFile.exists()) { "The pack at '$otherPath' does not contain a pack.mcmeta file." }

			val otherPackMCMeta = datapack.jsonEncoder.decodeFromString<PackMCMeta>(otherPackMCMetaFile.readText())
			if (!datapack.isCompatibleWith(otherPackMCMeta)) return@forEach

			val otherDataDir = otherPackFile.resolve("data")
			if (!otherDataDir.exists()) {
				println("The pack at '$otherPath' does not contain a data directory, skipping merge.")
				return@forEach
			}

			// Merge the data directory
			otherDataDir.toJavaFile().walkTopDown().forEach copyFiles@{ file ->
				val relativePath = Path(file).relativeTo(otherDataDir).asInvariantPathSeparator

				if (relativePath in tagsToMerge) {
					val tag = datapack.jsonEncoder.decodeFromString<Tag<TaggedResourceLocationArgument>>(file.readText())
					foundTags[relativePath]?.add(tag)
				}
				copyFile(file, relativePath, to = outputDataPath)
			}
		}

		// Merge the tags
		foundTags.forEach tags@{ (tagPath, tags) ->
			if (tags.isEmpty()) return@tags

			println("Merging tags of: $tagPath")

			val namespace = tagPath.substringBefore("/")
			val tagFilename = tagPath.substringAfterLast("/").removeSuffix(".json")
			val tagType = tagPath.substringAfter("/tags/").substringBefore("/")

			val targetTag =
				datapack.tags.find<Tag<TaggedResourceLocationArgument>> { it.namespace == namespace && it.fileName == tagFilename && it.type == tagType }

			val newTag = Tag<TaggedResourceLocationArgument>(fileName = tagFilename, type = tagType)
			newTag.namespace = namespace
			newTag.values = ((targetTag?.values ?: emptyList()) + tags.flatMap { it.values }).distinct()
			writeFile(tagPath, datapack.jsonEncoder.encodeToString(newTag), to = outputDataPath)
		}

		outputStream?.close()

		datapack.generated = true
	}

	fun init() {
		try {
			when (mode) {
				DatapackGenerationMode.FOLDER -> SystemFileSystem.createDirectories(outputPath)

				DatapackGenerationMode.JAR -> {
					val options = options as? DataPackJarGenerationOptions ?: return
					options.providers.forEach { it.generateAdditionalFiles(this, options) }
				}

				DatapackGenerationMode.ZIP -> {}
			}
		} catch (e: FileSystemException) {
			warn("An error occurred while trying to create the directory for the datapack '${datapack.name}': $e")
		}
	}

	fun copyFile(file: File, path: String, to: Path = outputDataPath) {
		val finalPath = to.resolve(path.replace("\\", "/"))

		when (mode) {
			DatapackGenerationMode.FOLDER -> {
				val newFile = finalPath
				newFile.ensureParents()
				if (newFile.isDirectory()) {
					newFile.makeDirectories()
					return
				}

				val sourcePath = Path(file.absolutePath.toString())
				val source = sourcePath.toSource().buffered()
				val sink = newFile.toSink()
				source.transferTo(sink)
				sink.flush()
			}

			DatapackGenerationMode.JAR, DatapackGenerationMode.ZIP -> outputStream?.let {
				if (file.isDirectory) return

				val relativePath =
					finalPath.toString().removePrefix(outputPath.toString()).removePrefix("/")

				it.putNextEntry(ZipEntry(relativePath))
				file.inputStream().use { input -> input.copyTo(it) }
				it.closeEntry()
			}
		}
	}

	fun writeFile(path: String, content: String, to: Path = outputPath) = writeFile(path, content.toByteArray(), to)

	fun writeFile(path: String, content: ByteArray, to: Path = outputPath) {
		val finalPath = to.resolve(path.replace("\\", "/"))

		when (mode) {
			DatapackGenerationMode.FOLDER -> {
				val file = to.resolve(finalPath)
				file.ensureParents()
				file.write(content)
			}

			DatapackGenerationMode.JAR, DatapackGenerationMode.ZIP -> outputStream?.let {
				val relativePath = finalPath.toString().removePrefix(outputPath.toString()).removePrefix("/")

				it.putNextEntry(ZipEntry(relativePath))
				it.write(content)
				it.closeEntry()
			}
		}
	}
}
