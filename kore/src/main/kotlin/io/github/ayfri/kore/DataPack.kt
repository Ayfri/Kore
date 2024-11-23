package io.github.ayfri.kore

import io.github.ayfri.kore.annotations.FunctionsHolder
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.advancements.Advancement
import io.github.ayfri.kore.features.bannerpatterns.BannerPattern
import io.github.ayfri.kore.features.chattypes.ChatType
import io.github.ayfri.kore.features.damagetypes.DamageType
import io.github.ayfri.kore.features.enchantments.Enchantment
import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvider
import io.github.ayfri.kore.features.instruments.Instrument
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
import io.github.ayfri.kore.generation.DataPackGenerationOptions
import io.github.ayfri.kore.generation.DataPackGenerator
import io.github.ayfri.kore.generation.DataPackJarGenerationOptions
import io.github.ayfri.kore.generation.DatapackGenerationMode
import io.github.ayfri.kore.pack.Features
import io.github.ayfri.kore.pack.Filter
import io.github.ayfri.kore.pack.Pack
import io.github.ayfri.kore.pack.PackMCMeta
import io.github.ayfri.kore.serializers.JsonNamingSnakeCaseStrategy
import io.github.ayfri.kore.utils.warn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories

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
	val instruments = registerGenerator<Instrument>()
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
		internal set
	var iconPath: Path? = null
	var path = Path("out")
	val pack = Pack(DEFAULT_DATAPACK_FORMAT, textComponent("Generated by Kore"))

	internal val cleanPath get() = path.absolute().normalize().createDirectories()

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

	fun generatePackMCMetaFile() = jsonEncoder.encodeToString(PackMCMeta(pack, features, filter))

	fun generate(init: DataPackGenerationOptions.() -> Unit = {}) {
		val options = DataPackGenerationOptions().apply(init)
		val datapackGenerator = DataPackGenerator(this, options)
		datapackGenerator.generate()
	}

	fun generateJar(init: DataPackJarGenerationOptions.() -> Unit = {}) {
		val options = DataPackJarGenerationOptions(this).apply(init)
		val datapackGenerator = DataPackGenerator(this, options, DatapackGenerationMode.JAR)
		datapackGenerator.generate()
	}

	fun generateZip(init: DataPackGenerationOptions.() -> Unit = {}) {
		val options = DataPackGenerationOptions().apply(init)
		val datapackGenerator = DataPackGenerator(this, options, DatapackGenerationMode.ZIP)
		datapackGenerator.generate()
	}

	fun isCompatibleWith(otherPack: PackMCMeta): Boolean {
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
