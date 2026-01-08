package io.github.ayfri.kore.bindings.api

import io.github.ayfri.kore.bindings.Datapack
import io.github.ayfri.kore.bindings.debugEnabled
import io.github.ayfri.kore.bindings.download.Downloaders
import io.github.ayfri.kore.bindings.explore
import io.github.ayfri.kore.bindings.generateDatapackFile
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Internal implementation for importing datapacks.
 * Use the `importDatapacks {}` DSL for public API.
 */
internal class DatapackImporter(val source: String) {
	var debug: Boolean = false
	var excludes: List<String> = emptyList()
	var includes: List<String> = emptyList()
	var outputDirectory: Path? = null
	var packageNameOverride: String? = null
	var remappedNameOverride: String? = null
	var skipCache: Boolean = false
	var subPath: String? = null

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
	 * Downloads the file if it's a URL or GitHub reference, or locates it on the local filesystem.
	 */
	private fun resolveSource() = Downloaders.download(source, skipCache)

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
}
