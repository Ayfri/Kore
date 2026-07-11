package io.github.ayfri.kore.generation

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generation.platform.*
import io.github.ayfri.kore.generation.zip.ZipWriter
import io.github.ayfri.kore.pack.PackMCMeta
import io.github.ayfri.kore.utils.*
import kotlinx.io.files.Path

enum class DatapackGenerationMode {
	FOLDER,
	JAR,
	ZIP,
}

/**
 * Builds the on-disk (JVM/Node.js) or in-memory (browser) representation of a [DataPack]. Use
 * [DataPack.generate]/[DataPack.generateJar]/[DataPack.generateZip] (synchronous, JVM/Node.js) or
 * [DataPack.generateZipBytes] (suspend, works everywhere including the browser) rather than this class directly.
 */
data class DataPackGenerator(
	var datapack: DataPack,
	var options: DataPackGenerationCommonOptions = DataPackGenerationOptions(),
	var mode: DatapackGenerationMode = DatapackGenerationMode.FOLDER,
) {
	private companion object {
		val tagsToMerge = setOf("minecraft/tags/function/load.json", "minecraft/tags/functions/tick.json")
	}

	/** On the browser there is no real filesystem to resolve/create against, only the logical configured path. */
	val dataPackPath: Path get() = if (platformRequiresSuspension) datapack.path else datapack.cleanPath

	val outputPath: Path
		get() = when (mode) {
			DatapackGenerationMode.FOLDER -> dataPackPath.resolve(datapack.name)
			DatapackGenerationMode.JAR -> dataPackPath.resolve("${datapack.name}.jar")
			DatapackGenerationMode.ZIP -> dataPackPath.resolve("${datapack.name}.zip")
		}

	val outputDataPath get() = outputPath.resolve("data")

	private val archive: ZipWriter? = when (mode) {
		DatapackGenerationMode.JAR, DatapackGenerationMode.ZIP -> ZipWriter()
		DatapackGenerationMode.FOLDER -> null
	}
	private val writtenArchiveEntries = mutableSetOf<String>()

	/**
	 * Generates the datapack. Returns the built archive bytes for [DatapackGenerationMode.JAR]/`.ZIP`
	 * (also written to [outputPath] as a side effect on JVM/Node.js), or `null` for [DatapackGenerationMode.FOLDER].
	 */
	suspend fun generate(): ByteArray? {
		if (datapack.generated) {
			warn("Trying to generate the already generated datapack '${datapack.name}' in $dataPackPath.")
			return null
		}

		val packMCMeta = datapack.generatePackMCMetaFile()

		init()
		writeFile("pack.mcmeta", packMCMeta)

		datapack.iconPath?.let { path -> platformReadFile(path)?.let { writeFile("pack.png", it) } }

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
				if (options.mergeWithPacks.isNotEmpty() && path.dataRelativePath() in tagsToMerge) return@forEach
				writeFile(path.toString(), generator.generateJson(datapack))
			}

		if (options.mergeWithPacks.isNotEmpty()) mergeWithOtherPacks()

		val bytes = archive?.toByteArray()
		if (bytes != null) platformWriteFile(outputPath, bytes)

		datapack.generated = true
		return bytes
	}

	private suspend fun mergeWithOtherPacks() {
		val mergeWithPacks = options.mergeWithPacks.sortedBy { it.name }
		println("Merging datapack with other packs: ${mergeWithPacks.joinToString(", ")}")

		val foundTags = tagsToMerge.associateWith { mutableListOf<Tag<TaggedResourceLocationArgument>>() }

		mergeWithPacks.forEach { otherPath ->
			require(platformExists(otherPath)) { "The pack at '$otherPath' does not exist." }

			var otherPackFile = otherPath
			if (otherPath.toString().endsWith(".zip")) {
				otherPackFile = platformUnzipToTempDir(otherPath)
				println("Unzipped pack '$otherPath' to: ${otherPackFile.absolute()}")
			}

			if (otherPackFile == dataPackPath) {
				println("Skipping merging with the current datapack.")
				return@forEach
			}

			val otherPackMCMetaFile = otherPackFile.resolve("pack.mcmeta")
			val otherPackMCMetaBytes = platformReadFile(otherPackMCMetaFile)
				?: error("The pack at '$otherPath' does not contain a pack.mcmeta file.")

			val otherPackMCMeta =
				datapack.jsonEncoder.decodeFromString<PackMCMeta>(otherPackMCMetaBytes.decodeToString())
			if (!datapack.isCompatibleWith(otherPackMCMeta)) return@forEach

			val otherDataDir = otherPackFile.resolve("data")
			if (!platformExists(otherDataDir)) {
				println("The pack at '$otherPath' does not contain a data directory, skipping merge.")
				return@forEach
			}

			copyDirectoryRecursively(otherDataDir, tagsToMerge, foundTags)
		}

		foundTags.forEach tags@{ (tagPath, tags) ->
			if (tags.isEmpty()) return@tags

			println("Merging tags of: $tagPath")

			val namespace = tagPath.substringBefore("/")
			val tagFilename = tagPath.substringAfterLast("/").removeSuffix(".json")
			val tagType = tagPath.substringAfter("/tags/").substringBefore("/")

			val targetTag =
				datapack.tags.find { it.namespace == namespace && it.fileName == tagFilename && it.type == tagType }

			val newTag = Tag<TaggedResourceLocationArgument>(fileName = tagFilename, type = tagType)
			newTag.namespace = namespace
			newTag.values = ((targetTag?.values ?: emptyList()) + tags.flatMap { it.values }).distinct()
			writeFile(tagPath, datapack.jsonEncoder.encodeToString(newTag), to = outputDataPath)
		}
	}

	private suspend fun copyDirectoryRecursively(
		otherDataDir: Path,
		tagsToMerge: Set<String>,
		foundTags: Map<String, MutableList<Tag<TaggedResourceLocationArgument>>>,
	) {
		platformWalk(otherDataDir).forEach { file ->
			val relativePath = file.relativeTo(otherDataDir).asInvariantPathSeparator

			if (relativePath in tagsToMerge) {
				val content = platformReadFile(file) ?: return@forEach
				val tag =
					datapack.jsonEncoder.decodeFromString<Tag<TaggedResourceLocationArgument>>(content.decodeToString())
				foundTags[relativePath]?.add(tag)
				return@forEach
			}

			val content = platformReadFile(file) ?: return@forEach
			writeFile(relativePath, content, to = outputDataPath)
		}
	}

	suspend fun init() {
		when (mode) {
			DatapackGenerationMode.FOLDER -> platformCreateDirectories(outputPath)

			DatapackGenerationMode.JAR -> {
				val options = options as? DataPackJarGenerationOptions ?: return
				options.providers.forEach { it.generateAdditionalFiles(this, options) }
			}

			DatapackGenerationMode.ZIP -> {}
		}
	}

	suspend fun writeFile(path: String, content: String, to: Path = outputPath) =
		writeFile(path, content.encodeToByteArray(), to)

	suspend fun writeFile(path: String, content: ByteArray, to: Path = outputPath) {
		val finalPath = to.resolve(path.normalizePath())

		when (mode) {
			DatapackGenerationMode.FOLDER -> platformWriteFile(finalPath, content)

			DatapackGenerationMode.JAR, DatapackGenerationMode.ZIP -> archive?.let {
				val entryPath = finalPath.archiveRelativePath()
				check(writtenArchiveEntries.add(entryPath)) {
					"Cannot write duplicate archive entry '$entryPath' while generating datapack '${datapack.name}'."
				}
				it.addEntry(entryPath, content)
			}
		}
	}

	private fun String.normalizePath() = replace("\\", "/")

	private fun Path.dataRelativePath() =
		toString().normalizePath().removePrefix(outputDataPath.toString().normalizePath()).removePrefix("/")

	private fun Path.archiveRelativePath() = toString()
		.removePrefix(outputPath.toString())
		.replace("\\", "/")
		.removePrefix("/")
}
