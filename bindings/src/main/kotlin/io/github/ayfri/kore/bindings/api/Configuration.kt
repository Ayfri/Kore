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
	@Deprecated("Use remappings { objectName(...) } instead", level = DeprecationLevel.WARNING)
	var remappedName: String?
		get() = remappings.objectName
		set(value) = if (value != null) remappings.objectName(value) else Unit
	/**
	 * Configuration for remapping namespaces within this datapack.
	 */
	internal val remappings = RemappingConfiguration()
	/**
	 * Select a subfolder within the downloaded datapack.
	 */
	var subPath: String? = null
	/**
	 * Configures namespace remappings for this datapack.
	 */
	fun remappings(block: RemappingConfiguration.() -> Unit) = remappings.apply(block)
}

/**
 * Configuration for remapping namespaces within a datapack.
 */
class RemappingConfiguration {
	/**
	 * Map of namespace names to their remapped names.
	 */
	internal val namespaces = mutableMapOf<String, String>()

	/**
	 * Override the generated Kotlin object name.
	 */
	var objectName: String? = null

	/**
	 * Checks if any remappings have been configured.
	 */
	fun hasRemappings() = namespaces.isNotEmpty() || objectName != null

	/**
	 * Remaps a namespace to a new name.
	 */
	fun namespace(namespace: String, remappedName: String) {
		namespaces[namespace] = remappedName
	}

	/**
	 * Renames the generated Kotlin object.
	 */
	fun objectName(name: String) {
		objectName = name
	}

	internal fun toState() = RemappingState(namespaces.toMap(), objectName)
}
