package io.github.ayfri.kore.bindings.api

import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Global configuration for all datapack imports.
 * This will be the default configuration unless overridden per datapack.
 */
data class ImportConfiguration(
	var debug: Boolean = false,
	var generateSingleFile: Boolean = true,
	var outputPath: Path = Path("build/generated/kore/imported"),
	var packagePrefix: String = "kore.dependencies",
	var skipCache: Boolean = false,
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
	 * Exclude files matching these glob patterns.
	 */
	var excludes: List<String> = emptyList()
	/**
	 * Include only files matching these glob patterns.
	 */
	var includes: List<String> = emptyList()
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
}
