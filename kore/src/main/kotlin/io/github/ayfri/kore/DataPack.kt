package io.github.ayfri.kore

import io.github.ayfri.kore.annotations.FunctionsHolder
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.advancements.Advancement
import io.github.ayfri.kore.features.bannerpatterns.BannerPattern
import io.github.ayfri.kore.features.catvariants.CatVariant
import io.github.ayfri.kore.features.chattypes.ChatType
import io.github.ayfri.kore.features.chickenvariants.ChickenVariant
import io.github.ayfri.kore.features.cowvariants.CowVariant
import io.github.ayfri.kore.features.damagetypes.DamageType
import io.github.ayfri.kore.features.dialogs.Dialog
import io.github.ayfri.kore.features.enchantments.Enchantment
import io.github.ayfri.kore.features.enchantments.providers.EnchantmentProvider
import io.github.ayfri.kore.features.frogvariants.FrogVariant
import io.github.ayfri.kore.features.instruments.Instrument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.jukeboxsongs.JukeboxSong
import io.github.ayfri.kore.features.loottables.LootTable
import io.github.ayfri.kore.features.paintingvariant.PaintingVariant
import io.github.ayfri.kore.features.pigvariants.PigVariant
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.features.testenvironments.TestEnvironmentFeature
import io.github.ayfri.kore.features.testinstances.TestInstanceFeature
import io.github.ayfri.kore.features.trimmaterial.TrimMaterial
import io.github.ayfri.kore.features.trimpattern.TrimPattern
import io.github.ayfri.kore.features.wolfsoundvariants.WolfSoundVariant
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
import io.github.ayfri.kore.utils.absolute
import io.github.ayfri.kore.utils.exists
import io.github.ayfri.kore.utils.makeDirectories
import io.github.ayfri.kore.utils.warn
import kotlinx.io.files.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
* Represents a datapack being built in memory. A [DataPack] collects
* features such as functions, tags, recipes and worldgen resources and
* can generate them on disk or package them into archives.
*
* Docs: https://kore.ayfri.com/docs/creating-a-datapack
*
* @param name the datapack identifier used as default namespace
*/
@FunctionsHolder
class DataPack(val name: String) {
	val generators = mutableListOf<MutableList<out Generator>>()

	val functions = mutableListOf<Function>()
	val generatedFunctions = mutableListOf<Function>()

	val advancements = registerGenerator<Advancement>()
	val bannerPatterns = registerGenerator<BannerPattern>()
	val biomes = registerGenerator<Biome>()
	val catVariants = registerGenerator<CatVariant>()
	val chatTypes = registerGenerator<ChatType>()
	val chickenVariants = registerGenerator<ChickenVariant>()
	val configuredCarvers = registerGenerator<ConfiguredCarver>()
	val configuredFeatures = registerGenerator<ConfiguredFeature>()
	val cowVariants = registerGenerator<CowVariant>()
	val damageTypes = registerGenerator<DamageType>()
	val densityFunctions = registerGenerator<DensityFunction>()
	val dialogs = registerGenerator<Dialog>()
	val dimensions = registerGenerator<Dimension>()
	val dimensionTypes = registerGenerator<DimensionType>()
	val enchantments = registerGenerator<Enchantment>()
	val enchantmentProviders = registerGenerator<EnchantmentProvider>()
	val flatLevelGeneratorPresets = registerGenerator<FlatLevelGeneratorPreset>()
	val frogVariants = registerGenerator<FrogVariant>()
	val instruments = registerGenerator<Instrument>()
	val itemModifiers = registerGenerator<ItemModifier>()
	val jukeboxSongs = registerGenerator<JukeboxSong>()
	val lootTables = registerGenerator<LootTable>()
	val noises = registerGenerator<Noise>()
	val noiseSettings = registerGenerator<NoiseSettings>()
	val paintingVariants = registerGenerator<PaintingVariant>()
	val pigVariants = registerGenerator<PigVariant>()
	val placedFeatures = registerGenerator<PlacedFeature>()
	val predicates = registerGenerator<Predicate>()
	val processorLists = registerGenerator<ProcessorList>()
	val recipes = registerGenerator<RecipeFile>()
	val structures = registerGenerator<Structure>()
	val structureSets = registerGenerator<StructureSet>()
	val tags = registerGenerator<Tag<*>>()
	val templatePools = registerGenerator<TemplatePool>()
	val testEnvironments = registerGenerator<TestEnvironmentFeature>()
	val testInstances = registerGenerator<TestInstanceFeature>()
	val trimMaterials = registerGenerator<TrimMaterial>()
	val trimPatterns = registerGenerator<TrimPattern>()
	val wolfSoundVariants = registerGenerator<WolfSoundVariant>()
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

	internal val cleanPath get() = path.let {
		if (!it.exists()) it.makeDirectories()
		it.absolute()
	}

	private fun <T : Generator> registerGenerator() = mutableListOf<T>().also { generators += it }

	/**
	* Adds a user-defined function to the datapack.
	* Returns the same function as a [FunctionArgument] for convenience.
	*/
	fun addFunction(function: Function): FunctionArgument {
		functions += function
		return function
	}

	/**
	* Adds or reuses a generated function. Two generated functions with the
	* exact same body will be merged and the first instance will be returned.
	*/
	fun addGeneratedFunction(function: Function): FunctionArgument {
		generatedFunctions.find { it.lines == function.lines }?.let {
			return@addGeneratedFunction it
		}

		generatedFunctions += function
		return function
	}

	/** Generates the `pack.mcmeta` file. */
	fun generatePackMCMetaFile() = jsonEncoder.encodeToString(PackMCMeta(pack, features, filter))

	/** Generates the datapack as raw files. */
	fun generate(init: DataPackGenerationOptions.() -> Unit = {}) {
		val options = DataPackGenerationOptions().apply(init)
		val datapackGenerator = DataPackGenerator(this, options)
		datapackGenerator.generate()
	}

	/** Generates the datapack as a jar file, must be used as a mod. */
	fun generateJar(init: DataPackJarGenerationOptions.() -> Unit = {}) {
		val options = DataPackJarGenerationOptions(this).apply(init)
		val datapackGenerator = DataPackGenerator(this, options, DatapackGenerationMode.JAR)
		datapackGenerator.generate()
	}

	/** Generates the datapack as a zip file for easy distribution and faster loading. */
	fun generateZip(init: DataPackGenerationOptions.() -> Unit = {}) {
		val options = DataPackGenerationOptions().apply(init)
		val datapackGenerator = DataPackGenerator(this, options, DatapackGenerationMode.ZIP)
		datapackGenerator.generate()
	}

	/** Checks if the datapack is compatible with the other pack by comparing the pack format. */
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
		/** The default folder where the generated functions are stored, can be changed with [Configuration.generatedFunctionsFolder]. */
		const val DEFAULT_GENERATED_FUNCTIONS_FOLDER = "generated_scopes"
	}
}

/** Creates a new [DataPack] and applies the provided configuration block. */
fun dataPack(name: String, block: DataPack.() -> Unit) = DataPack(name).apply(block)

/** Edits the datapack configuration with the result of the given block. */
fun DataPack.configuration(block: Configuration.() -> Unit) {
	configuration = Configuration().apply(block)
}
