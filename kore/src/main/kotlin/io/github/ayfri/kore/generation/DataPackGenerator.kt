package io.github.ayfri.kore.generation

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.pack.PackMCMeta
import io.github.ayfri.kore.utils.warn
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileOutputStream
import java.nio.file.FileSystemException
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.pathString

internal fun unzip(zipFile: File): File {
	val cleanName = zipFile.nameWithoutExtension.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
	val tempDir = Files.createTempDirectory("kore_unzipped_datapack_$cleanName").toFile()
	ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
		var entry = zipInputStream.nextEntry
		while (entry != null) {
			val filePath = File(tempDir, entry.name)
			if (entry.isDirectory) {
				filePath.mkdirs()
			} else {
				// Ensure parent directories exist
				filePath.parentFile.mkdirs()
				// Copy the file content
				filePath.outputStream().use(zipInputStream::copyTo)
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
		DatapackGenerationMode.ZIP -> ZipOutputStream(FileOutputStream(outputPath.toFile()))
		DatapackGenerationMode.JAR -> JarOutputStream(FileOutputStream(outputPath.toFile()))
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

		datapack.iconPath?.let { writeFile("pack.png", it.toFile().readBytes()) }

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
				val path = outputDataPath.resolve(namespace).resolve(generator.resourceFolder).resolve("${generator.fileName}.json")
				writeFile(path.toString(), generator.generateJson(datapack))
			}

		val end = System.currentTimeMillis()
		println("Generated data pack '${datapack.name}' in ${end - start}ms in: $dataPackPath")

		if (options.mergeWithPacks.isEmpty()) {
			outputStream?.close()
			datapack.generated = true
			return
		}

		val mergeWithPacks = options.mergeWithPacks.sortedBy { it.fileName.toString() }
		println("Merging datapack with other packs: ${mergeWithPacks.joinToString(", ")}")

		val tagsToMerge = listOf("minecraft/tags/function/load.json", "minecraft/tags/functions/tick.json")
		val foundTags = tagsToMerge.associateWith { mutableListOf<Tag<TaggedResourceLocationArgument>>() }

		mergeWithPacks.forEach { otherPath ->
			require(otherPath.exists()) { "The pack at '$otherPath' does not exist." }

			var otherPackFile = otherPath.toFile()
			if (otherPath.pathString.endsWith(".zip")) {
				otherPackFile = unzip(otherPath.toFile())
				println("Unzipped pack '$otherPath' to: ${otherPackFile.absolutePath}")
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
			otherDataDir.walkTopDown().forEach copyFiles@{ file ->
				val relativePath = file.relativeTo(otherDataDir).toPath().invariantSeparatorsPathString

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
				DatapackGenerationMode.FOLDER -> outputPath.createDirectories()

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
				val newFile = finalPath.toFile()
				newFile.parentFile.mkdirs()
				if (newFile.isDirectory) {
					newFile.mkdir()
					return
				}

				file.copyTo(newFile, overwrite = true)
			}

			DatapackGenerationMode.JAR, DatapackGenerationMode.ZIP -> outputStream?.let {
				if (file.isDirectory) return

				val relativePath =
					finalPath.invariantSeparatorsPathString.removePrefix(outputPath.invariantSeparatorsPathString).removePrefix("/")

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
				val file = to.resolve(finalPath).toFile()
				file.parentFile.mkdirs()
				file.writeBytes(content)
			}

			DatapackGenerationMode.JAR, DatapackGenerationMode.ZIP -> outputStream?.let {
				val relativePath =
					finalPath.invariantSeparatorsPathString.removePrefix(outputPath.invariantSeparatorsPathString).removePrefix("/")

				it.putNextEntry(ZipEntry(relativePath))
				it.write(content)
				it.closeEntry()
			}
		}
	}
}
