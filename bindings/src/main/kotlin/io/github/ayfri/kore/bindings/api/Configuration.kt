package io.github.ayfri.kore.bindings.api

import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Global configuration for all datapack imports.
 * This will be the default configuration unless overridden per datapack.
 */
data class ImportConfiguration(
	var outputPath: Path = Path("build/generated/kore/imported"),
	var packagePrefix: String = "kore.dependencies",
	var generateSingleFile: Boolean = true,
	var skipCache: Boolean = false,
	var debug: Boolean = false,
) {
	/**
	 * Sets the output path for generated Kotlin files.
	 */
	fun outputPath(path: String) {
		outputPath = Path(path)
	}

	/**
	 * Sets the output path for generated Kotlin files.
	 */
	fun outputPath(path: Path) {
		outputPath = path
	}
}

/**
 * Per-datapack configuration.
 * Can override global configuration for specific datapacks.
 */
class DatapackConfiguration {
	/**
	 * Override the package name for a specific datapack.
	 */
	var packageName: String? = null
	/**
	 * Override the generated Kotlin object name.
	 */
	var remappedName: String? = null
	/**
	 * Select a subfolder within the downloaded datapack.
	 */
	var subPath: String? = null
	/**
	 * Include only files matching these glob patterns.
	 */
	var includes: List<String> = emptyList()
	/**
	 * Exclude files matching these glob patterns.
	 */
	var excludes: List<String> = emptyList()
}
