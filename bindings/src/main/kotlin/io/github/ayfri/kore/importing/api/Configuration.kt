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
	fun outputPath(path: String) {
		outputPath = Path(path)
	}

	fun outputPath(path: Path) {
		outputPath = path
	}
}

/**
 * Per-datapack configuration.
 * Can override global configuration for specific datapacks.
 */
data class DatapackConfiguration(
	var packageName: String? = null,
	var remappedName: String? = null,
)
