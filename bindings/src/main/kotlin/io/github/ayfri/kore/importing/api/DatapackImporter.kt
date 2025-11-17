package io.github.ayfri.kore.bindings.api

import io.github.ayfri.kore.bindings.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Internal implementation for importing datapacks.
 * Use the `importDatapacks {}` DSL for public API.
 */
internal class DatapackImporter(val source: String) {
	var outputDirectory: Path? = null
	var packageNameOverride: String? = null
	var remappedNameOverride: String? = null
	var skipCache: Boolean = false
	var debug: Boolean = false

	/**
	 * Sets the output directory for generated Kotlin code.
	 * If not set, defaults to "src/main/kotlin" in the current working directory.
	 */
	fun outputPath(path: String) = outputPath(Path(path))

	/**
	 * Sets the output directory for generated Kotlin code.
	 * If not set, defaults to "src/main/kotlin" in the current working directory.
	 */
	fun outputPath(path: Path) = apply { outputDirectory = path	}

	/**
	 * Resolves the source to a local path and returns the actual filename.
	 * Downloads the file if it's a URL, or locates it on the local filesystem.
	 */
	private fun resolveSource(): Pair<Path, String> = if (isUrl(source)) {
		downloadDatapack(source)
	} else {
		val path = locateLocalDatapack(source)
		path to path.fileName.toString()
	}

	/**
	 * Explores the datapack structure and returns metadata without generating code.
	 * Useful for inspecting what will be generated before committing to it.
	 */
	fun explore(): Datapack {
		debugEnabled = debug
		val (path, actualFileName) = resolveSource()
		val datapack = explore(path.toString())
		// Keep the original filename in the datapack, don't override with remappedName
		// The remappedName is only used for the Kotlin object name, not for package calculation
		return datapack.copy(name = actualFileName)
	}

	/**
	 * Explores and immediately generates Kotlin bindings for the datapack.
	 * This is a convenience method combining explore() and write().
	 */
	fun import(): Datapack {
		val datapack = explore()
		write(datapack)
		return datapack
	}

	/**
	 * Writes Kotlin bindings for an already-explored datapack.
	 * Use this after calling explore() if you want to process the datapack metadata first.
	 */
	fun write(datapack: Datapack) {
		val outputPath = outputDirectory ?: Path("src/main/kotlin")
		generateDatapackFile(datapack, outputPath, packageNameOverride, remappedNameOverride)
	}

	private fun downloadDatapack(url: String) = getFromCacheOrDownload(url, skipCache)

	private fun isUrl(source: String) = source.startsWith("http://") || source.startsWith("https://")

	private fun locateLocalDatapack(source: String): Path {
		// Try direct path first
		var path = Path(source)
		if (Files.exists(path)) return path

		// Try with .zip extension
		val withZip = Path("$source.zip")
		if (Files.exists(withZip)) return withZip

		// Try looking in common datapack locations
		val commonLocations = listOf(
			Path("./$source"),
			Path("./$source.zip"),
		)

		for (location in commonLocations) {
			if (Files.exists(location)) {
				println("Found datapack at: $location")
				return location
			}
		}

		throw IllegalArgumentException(
			"Could not locate datapack '$source'. Tried:\n" +
			"  - $path\n" +
			"  - $withZip\n" +
			commonLocations.joinToString("\n") { "  - $it" }
		)
	}
}
