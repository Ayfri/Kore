package io.github.ayfri.kore

import io.github.ayfri.kore.annotations.FunctionsHolder
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.advancements.Advancement
import io.github.ayfri.kore.features.bannerpatterns.BannerPattern
import io.github.ayfri.kore.features.chattypes.ChatType
import io.github.ayfri.kore.features.damagetypes.DamageType
import io.github.ayfri.kore.features.enchantments.Enchantment
import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvider
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.jukeboxsongs.JukeboxSong
import io.github.ayfri.kore.features.loottables.LootTable
import io.github.ayfri.kore.features.paintingvariant.PaintingVariant
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.features.wolfvariants.WolfVariant
import io.github.ayfri.kore.features.worldgen.biome.Biome
import io.github.ayfri.kore.features.worldgen.configuredcarver.ConfiguredCarver
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.dimension.Dimension
import io.github.ayfri.kore.features.worldgen.dimensiontype.DimensionType
import io.github.ayfri.kore.features.worldgen.flatlevelgeneratorpreset.FlatLevelGeneratorPreset
import io.github.ayfri.kore.features.worldgen.noise.Noise
import io.github.ayfri.kore.features.worldgen.noisesettings.NoiseSettings
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import io.github.ayfri.kore.features.worldgen.processorlist.ProcessorList
import io.github.ayfri.kore.features.worldgen.structures.Structure
import io.github.ayfri.kore.features.worldgen.structureset.StructureSet
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePool
import io.github.ayfri.kore.features.worldgen.worldpreset.WorldPreset
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.DEFAULT_DATAPACK_FORMAT
import io.github.ayfri.kore.pack.Features
import io.github.ayfri.kore.pack.Filter
import io.github.ayfri.kore.pack.Pack
import io.github.ayfri.kore.pack.PackMCMeta
import io.github.ayfri.kore.serializers.JsonNamingSnakeCaseStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

@FunctionsHolder
class DataPack(val name: String) {
	val generators = mutableListOf<MutableList<out Generator>>()

	val functions = mutableListOf<Function>()
	val generatedFunctions = mutableListOf<Function>()

	val advancements = registerGenerator<Advancement>()
	val bannerPatterns = registerGenerator<BannerPattern>()
	val biomes = registerGenerator<Biome>()
	val chatTypes = registerGenerator<ChatType>()
	val configuredCarvers = registerGenerator<ConfiguredCarver>()
	val configuredFeatures = registerGenerator<ConfiguredFeature>()
	val damageTypes = registerGenerator<DamageType>()
	val densityFunctions = registerGenerator<DensityFunction>()
	val dimensions = registerGenerator<Dimension>()
	val dimensionTypes = registerGenerator<DimensionType>()
	val enchantments = registerGenerator<Enchantment>()
	val enchantmentProviders = registerGenerator<EnchantmentProvider>()
	val flatLevelGeneratorPresets = registerGenerator<FlatLevelGeneratorPreset>()
	val itemModifiers = registerGenerator<ItemModifier>()
	val jukeboxSongs = registerGenerator<JukeboxSong>()
	val lootTables = registerGenerator<LootTable>()
	val noises = registerGenerator<Noise>()
	val noiseSettings = registerGenerator<NoiseSettings>()
	val paintingVariants = registerGenerator<PaintingVariant>()
	val placedFeatures = registerGenerator<PlacedFeature>()
	val predicates = registerGenerator<Predicate>()
	val processorLists = registerGenerator<ProcessorList>()
	val recipes = registerGenerator<RecipeFile>()
	val structures = registerGenerator<Structure>()
	val structureSets = registerGenerator<StructureSet>()
	val tags = registerGenerator<Tag<*>>()
	val templatePools = registerGenerator<TemplatePool>()
	val wolfVariants = registerGenerator<WolfVariant>()
	val worldPresets = registerGenerator<WorldPreset>()

	var configuration = Configuration.DEFAULT
	var features = Features()
	var filter: Filter? = null
	var generated = false
		private set
	var iconPath: Path? = null
	var path = Path("out")
	val pack = Pack(DEFAULT_DATAPACK_FORMAT, textComponent("Generated by Kore"))

	private fun <T : Generator> registerGenerator() = mutableListOf<T>().also { generators += it }

	fun addFunction(function: Function): FunctionArgument {
		functions += function
		return function
	}

	fun addGeneratedFunction(function: Function): FunctionArgument {
		generatedFunctions.find { it.lines == function.lines }?.let {
			return@addGeneratedFunction it
		}

		generatedFunctions += function
		return function
	}

	private val cleanPath get() = path.absolute().normalize().createDirectories()

	fun generate(init: DataPackGenerationOptions.() -> Unit = {}) {
		val options = DataPackGenerationOptions().apply(init)
		val start = System.currentTimeMillis()
		val root = File("$cleanPath/$name")
		root.mkdirs()

		val packMCMeta = generatePackMCMetaFile()
		File(root, "pack.mcmeta").writeText(packMCMeta)
		iconPath?.let { File(root, "pack.png").writeBytes(it.toFile().readBytes()) }

		val data = File(root, "data")
		data.mkdirs()

		generators.forEach { generator ->
			generator.forEach { it.generateFile(this) }
		}

		data.generateFunctions("function", functions.groupBy(Function::namespace))
		data.generateFunctions(
			dirName = "function/${configuration.generatedFunctionsFolder}",
			functionsMap = generatedFunctions.map {
				it.directory = it.directory.removePrefix(configuration.generatedFunctionsFolder)
				it
			}.groupBy(Function::namespace),
			deleteOldFiles = true
		)
		val end = System.currentTimeMillis()
		println("Generated data pack '$name' in ${end - start}ms in: ${root.absolutePath}")

		if (options.mergeWithPacks.isEmpty()) return

		val mergeWithPacks = options.mergeWithPacks.sortedBy { it.fileName.toString() }
		println("Merging with other packs: ${mergeWithPacks.joinToString(", ")}")

		val tagsToMerge = listOf("minecraft/tags/function/load.json", "minecraft/tags/functions/tick.json")
			.map { it.replace("/", File.separator) }
		val foundTags = tagsToMerge.associateWith { mutableListOf<Tag<TaggedResourceLocationArgument>>() }

		mergeWithPacks.forEach { otherPath ->
			require(otherPath.exists()) { "The pack at '$otherPath' does not exist." }

			var otherPackFile = otherPath.toFile()
			if (otherPath.endsWith(".zip")) {
				otherPackFile = unzip(otherPath.toFile())
			}

			if (otherPackFile == cleanPath) {
				println("The pack at '$otherPath' is the same as the current one, skipping merge.")
				return@forEach
			}

			val otherPackMCMetaFile = otherPackFile.resolve("pack.mcmeta")
			require(otherPackMCMetaFile.exists()) { "The pack at '$otherPath' does not contain a pack.mcmeta file." }

			val otherPackMCMeta = jsonEncoder.decodeFromString<PackMCMeta>(otherPackMCMetaFile.readText())
			if (!versionsCompatibles(otherPackMCMeta)) return@forEach

			val otherDataDir = otherPackFile.resolve("data")

			if (!otherDataDir.exists()) {
				println("The pack at '$otherPath' does not contain a data directory, skipping merge.")
				return@forEach
			}

			// Merge the data directory
			otherDataDir.walkTopDown().forEach copyFiles@{ file ->
				val relativePath = file.relativeTo(otherDataDir).toString()
				val targetFile = data.resolve(relativePath)
				if (file.isFile) {
					if (relativePath in tagsToMerge) {
						val tag = jsonEncoder.decodeFromString<Tag<TaggedResourceLocationArgument>>(file.readText())
						foundTags[relativePath]?.add(tag)
					}

					if (targetFile.exists()) {
						// If the file already exists, skip it
						return@copyFiles
					}

					file.copyTo(targetFile)
				} else {
					targetFile.mkdirs()
				}
			}

			// Merge the tags
			foundTags.forEach tags@{ (tagPath, tags) ->
				if (tags.isEmpty()) return@tags

				println("Merging tags of: $tagPath")

				val targetFile = data.resolve(tagPath)
				if (targetFile.exists()) {
					val currentTagFile = jsonEncoder.decodeFromString<Tag<TaggedResourceLocationArgument>>(targetFile.readText())
					val values = currentTagFile.values.toMutableList()
					tags.forEach { tag ->
						tag.values.forEach { value ->
							if (value !in values) {
								values += value
							}
						}
					}
					currentTagFile.values = values
					targetFile.writeText(jsonEncoder.encodeToString(currentTagFile))
				} else {
					targetFile.writeText(jsonEncoder.encodeToString(tags))
				}
			}
		}

		generated = true
	}

	private fun versionsCompatibles(otherPack: PackMCMeta): Boolean {
		val yellow = "\u001B[33m"
		val reset = "\u001B[0m"
		fun warn(message: String) = println("$yellow$message$reset")

		if (otherPack.pack.format != pack.format) {
			val packFormatPrint = "Format: current: ${pack.format} other: ${otherPack.pack.format}."
			if (otherPack.pack.supportedFormats != null && pack.supportedFormats != null) {
				if (otherPack.pack.supportedFormats!!.isCompatibleWith(pack.supportedFormats!!)) {
					return true
				}

				warn("The pack format of the other pack is different from the current one and the supported formats are different. This may cause issues.")
				warn(packFormatPrint)
				warn("Supported Formats: current: ${pack.supportedFormats} other: ${otherPack.pack.supportedFormats}.")
				return false
			}

			warn("The pack format of the other pack is different from the current one. This may cause issues.")
			warn(packFormatPrint)
			return false
		}
		return true
	}

	fun generatePackMCMetaFile() = jsonEncoder.encodeToString(PackMCMeta(pack, features, filter))

	fun generateZip(init: DataPackGenerationOptions.() -> Unit = {}) {
		if (generated) return
		val options = DataPackGenerationOptions().apply(init)
		val start = System.currentTimeMillis()

		val zip = File("$cleanPath/$name.zip")
		zip.delete()
		zip.createNewFile()
		ZipOutputStream(FileOutputStream(zip)).use { zipOutputStream ->
			zipOutputStream.putNextEntry(ZipEntry("pack.mcmeta"))
			zipOutputStream.write(generatePackMCMetaFile().toByteArray())
			zipOutputStream.closeEntry()

			iconPath?.let {
				zipOutputStream.putNextEntry(ZipEntry("pack.png"))
				zipOutputStream.write(it.toFile().readBytes())
			}

			functions.distinctBy(Function::getFinalPath).forEach { function ->
				zipOutputStream.putNextEntry(ZipEntry(function.getFinalPath().replace("\\", "/")))
				zipOutputStream.write(function.lines.joinToString("\n").toByteArray())
				zipOutputStream.closeEntry()
			}

			generatedFunctions.distinctBy(Function::getFinalPath).forEach { function ->
				zipOutputStream.putNextEntry(ZipEntry(function.getFinalPath().replace("\\", "/")))
				zipOutputStream.write(function.lines.joinToString("\n").toByteArray())
				zipOutputStream.closeEntry()
			}

			generators.flatten()
				.distinctBy { it.getFinalPath(this) }
				.forEach { generator -> generator.generateZipEntry(this, zipOutputStream) }


			val end = System.currentTimeMillis()
			println("Generated data pack '$name' in ${end - start}ms in: ${zip.absolutePath}")

			if (options.mergeWithPacks.isEmpty()) return

			val mergeWithPacks = options.mergeWithPacks.sortedBy { it.fileName.toString() }
			println("Merging datapack '$name' with other packs: ${mergeWithPacks.joinToString(", ")}")
			mergeWithPacks.forEach { otherPath ->
				require(otherPath.exists()) { "The pack at '$otherPath' does not exist." }

				var otherPackFile = otherPath.toFile()
				if (otherPath.endsWith(".zip")) {
					otherPackFile = unzip(otherPath.toFile())
				}

				if (otherPackFile == cleanPath) {
					println("The pack at '$otherPath' is the same as the current one, skipping merge.")
					return@forEach
				}

				val otherPackMCMetaFile = otherPackFile.resolve("pack.mcmeta")
				require(otherPackMCMetaFile.exists()) { "The pack at '$otherPath' does not contain a pack.mcmeta file." }

				val otherPackMCMeta = jsonEncoder.decodeFromString<PackMCMeta>(otherPackMCMetaFile.readText())
				if (!versionsCompatibles(otherPackMCMeta)) return@forEach

				val otherDataDir = otherPackFile.resolve("data")

				if (!otherDataDir.exists()) {
					println("The pack at '$otherPath' does not contain a data directory, skipping merge.")
					return@forEach
				}

				// Merge the data directory
				otherDataDir.walkTopDown().forEach copyFiles@{ file ->
					val relativePath = file.relativeTo(otherDataDir).toString()
					zipOutputStream.putNextEntry(ZipEntry("data/${relativePath.replace("\\", "/")}"))
					if (file.isFile) {
						file.inputStream().use { it.copyTo(zipOutputStream) }
					}
					zipOutputStream.closeEntry()
				}
			}
		}
	}

	private fun unzip(zipFile: File): File {
		val tempDir = Files.createTempDirectory("kore_datapacks_unzipped").toFile()
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

		return tempDir.resolve(zipFile.nameWithoutExtension)
	}

	private fun File.generateFunctions(
		dirName: String,
		functionsMap: Map<String, List<Function>>,
		deleteOldFiles: Boolean = false,
	) = functionsMap.forEach { (namespace, functions) ->
		if (functions.isEmpty()) return

		val namespaceDir = File(this, namespace)
		namespaceDir.mkdirs()

		val functionsNamespacedDir = File(namespaceDir, dirName)
		if (deleteOldFiles) functionsNamespacedDir.deleteRecursively()
		functionsNamespacedDir.mkdirs()

		functions.forEach { it.generate(functionsNamespacedDir) }
	}

	@OptIn(ExperimentalSerializationApi::class)
	val jsonEncoder
		get() = Json {
			prettyPrint = configuration.prettyPrint
			if (prettyPrint) prettyPrintIndent = configuration.prettyPrintIndent
			encodeDefaults = true
			explicitNulls = false
			ignoreUnknownKeys = true
			namingStrategy = JsonNamingSnakeCaseStrategy
			useAlternativeNames = false
		}

	companion object {
		const val DEFAULT_GENERATED_FUNCTIONS_FOLDER = "generated_scopes"
	}
}

fun dataPack(name: String, block: DataPack.() -> Unit) = DataPack(name).apply(block)

fun DataPack.configuration(block: Configuration.() -> Unit) {
	configuration = Configuration().apply(block)
}
