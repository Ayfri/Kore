package io.github.ayfri.kore.bindings.api

import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths


/**
 * DSL builder for configuring multiple datapack imports.
 * Inspired by Gradle plugin syntax.
 *
 * Example:
 * ```kotlin
 * importDatapacks {
 *     configuration {
 *         outputPath("src/main/kotlin")
 *         packagePrefix("com.example.datapacks")
 *     }
 *
 *     url("my_datapack") {
 *         packageName("com.example.custom")
 *     }
 *
 *     url("https://example.com/other.zip") {
 *         remapName("OtherPack")
 *     }
 * }
 * ```
 *
 * Docs: [Bindings](https://kore.ayfri.com/docs/advanced/bindings)
 */
class DatapackImportDsl {
	private val globalConfig = ImportConfiguration()
	private val datapacks = mutableListOf<Pair<String, DatapackConfiguration>>()

	/**
	 * Configure global import settings.
	 */
	fun configuration(block: ImportConfiguration.() -> Unit) = globalConfig.apply(block)

	/**
	 * Add a datapack from CurseForge.
	 *
	 * Patterns:
	 * - Project ID: `curseforge:123456`
	 * - Project ID + File ID: `curseforge:123456:789`
	 * - Slug: `curseforge:my-pack`
	 * - Slug + File ID: `curseforge:my-pack:789`
	 * - URL: `curseforge:https://www.curseforge.com/minecraft/data-packs/my-pack`
	 *
	 * Requires `CURSEFORGE_API_KEY` environment variable.
	 */
	fun curseforge(id: String, block: DatapackConfiguration.() -> Unit = {}) {
		val config = DatapackConfiguration().apply(block)
		datapacks.add("curseforge:$id" to config)
	}

	/**
	 * Add a datapack from GitHub.
	 *
	 * Patterns:
	 * - Repository download: `user.repo:tag` (tag can be a release tag, commit, or branch)
	 * - Release asset: `user.repo:tag:asset-name.zip`
	 * - Default branch: `user.repo` (no tag specified)
	 *
	 * Examples:
	 * ```
	 * github("ayfri.my-datapack") // Downloads default branch
	 * github("ayfri.my-datapack:v1.0.0") // Downloads release tag v1.0.0
	 * github("ayfri.my-datapack:main") // Downloads main branch
	 * github("ayfri.my-datapack:abc123") // Downloads specific commit
	 * github("ayfri.my-datapack:v1.0.0:pack.zip") // Downloads specific asset from release
	 * ```
	 */
	fun github(reference: String, block: DatapackConfiguration.() -> Unit = {}) {
		val config = DatapackConfiguration().apply(block)
		datapacks.add("github:$reference" to config)
	}

	/**
	 * Add a datapack from Modrinth.
	 *
	 * Patterns:
	 * - Latest version: `modrinth:slug`
	 * - Specific version: `modrinth:slug:version`
	 */
	fun modrinth(id: String, block: DatapackConfiguration.() -> Unit = {}) {
		val config = DatapackConfiguration().apply(block)
		datapacks.add("modrinth:$id" to config)
	}

	/**
	 * Add a datapack to import from a URL or local path.
	 */
	fun url(source: String, block: DatapackConfiguration.() -> Unit = {}) {
		val config = DatapackConfiguration().apply(block)
		datapacks.add(source to config)
	}

	/**
	 * Add a datapack to import from an URL or local path.
	 */
	fun url(source: URL, block: DatapackConfiguration.() -> Unit = {}) = url(Paths.get(source.toURI()).toString(), block)

	/**
	 * Add a datapack to import from a local file path.
	 */
	fun url(path: Path, block: DatapackConfiguration.() -> Unit = {}) = url(path.toString(), block)

	/**
	 * Execute the import process for all configured datapacks.
	 */
	internal fun execute() = datapacks.map { (source, datapackConfig) ->
		val importer = DatapackImporter(source)
		importer.outputPath(globalConfig.outputPath)
		importer.skipCache = globalConfig.skipCache
		importer.debug = globalConfig.debug

		// Apply per-datapack configuration
		if (datapackConfig.packageName != null) {
			importer.packageNameOverride = datapackConfig.packageName!!
		}
		if (datapackConfig.subPath != null) {
			importer.subPath = datapackConfig.subPath!!
		}
		if (datapackConfig.includes.isNotEmpty()) {
			importer.includes = datapackConfig.includes
		}
		if (datapackConfig.excludes.isNotEmpty()) {
			importer.excludes = datapackConfig.excludes
		}
		if (datapackConfig.remappings.hasRemappings()) {
			importer.remappings = datapackConfig.remappings.toState()
		}

		// If no custom package name, use global prefix with datapack name
		if (datapackConfig.packageName == null) {
			// Explore to get the actual datapack name
			val datapack = importer.explore()
			val baseName = datapack.name.removeSuffix(".zip").replace(".", "-")
			val normalizedName = baseName.lowercase().replace(Regex("[^a-z0-9]"), "")
			importer.packageNameOverride = "${globalConfig.packagePrefix}.$normalizedName"
			// Write the already-explored datapack
			importer.write(datapack)
			return@map datapack
		}

		importer.import()
	}

	/**
	 * Explore all configured datapacks without generating code.
	 */
	internal fun exploreAll() = datapacks.map { (source, _) ->
		DatapackImporter(source).explore()
	}
}

/**
 * Main entry point for the DSL-based datapack importing.
 *
 * Example:
 * ```kotlin
 * importDatapacks {
 *     configuration {
 *         generateSingleFile = true
 *         outputPath("src/main/kotlin")
 *         packagePrefix("com.example.datapacks")
 *     }
 *
 *     url("my_datapack")
 *
 *     url("https://example.com/pack.zip") {
 *         remapName("CustomPack")
 *     }
 * }
 * ```
 *
 *  Docs: [Bindings](https://kore.ayfri.com/docs/advanced/bindings)
 */
fun importDatapacks(block: DatapackImportDsl.() -> Unit) = DatapackImportDsl().apply(block).execute()

/**
 * Explore multiple datapacks without generating code.
 *
 * Example:
 * ```kotlin
 * val datapacks = exploreDatapacks {
 *     url("my_datapack")
 *     url("another_pack")
 * }
 * ```
 *
 *  Docs: [Bindings](https://kore.ayfri.com/docs/advanced/bindings)
 */
fun exploreDatapacks(block: DatapackImportDsl.() -> Unit) = DatapackImportDsl().apply(block).exploreAll()
