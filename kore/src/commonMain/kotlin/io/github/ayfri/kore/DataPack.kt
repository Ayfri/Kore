package io.github.ayfri.kore

import io.github.ayfri.kore.annotations.FunctionsHolder
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.features.advancements.Advancement
import io.github.ayfri.kore.features.bannerpatterns.BannerPattern
import io.github.ayfri.kore.features.catsoundvariants.CatSoundVariant
import io.github.ayfri.kore.features.catvariants.CatVariant
import io.github.ayfri.kore.features.chattypes.ChatType
import io.github.ayfri.kore.features.chickensoundvariants.ChickenSoundVariant
import io.github.ayfri.kore.features.chickenvariants.ChickenVariant
import io.github.ayfri.kore.features.cowsoundvariants.CowSoundVariant
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
import io.github.ayfri.kore.features.pigsoundvariants.PigSoundVariant
import io.github.ayfri.kore.features.pigvariants.PigVariant
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.tags.Tag
import io.github.ayfri.kore.features.testenvironments.TestEnvironmentFeature
import io.github.ayfri.kore.features.testinstances.TestInstanceFeature
import io.github.ayfri.kore.features.timelines.Timeline
import io.github.ayfri.kore.features.tradesets.TradeSet
import io.github.ayfri.kore.features.trimmaterial.TrimMaterial
import io.github.ayfri.kore.features.trimpattern.TrimPattern
import io.github.ayfri.kore.features.villagertrades.VillagerTrade
import io.github.ayfri.kore.features.wolfsoundvariants.WolfSoundVariant
import io.github.ayfri.kore.features.wolfvariants.WolfVariant
import io.github.ayfri.kore.features.worldclock.WorldClock
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
import io.github.ayfri.kore.features.zombienautilusvariants.ZombieNautilusVariant
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.DEFAULT_PACK_FORMAT
import io.github.ayfri.kore.pack.*
import io.github.ayfri.kore.serializers.JsonNamingSnakeCaseStrategy
import io.github.ayfri.kore.utils.*
import kotlinx.io.files.Path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
* Represents a datapack being built in memory. A [DataPack] collects
* features such as functions, tags, recipes and worldgen resources and
* can generate them on disk or package them into archives.
*
 * Docs: https://kore.ayfri.com/docs/guides/creating-a-datapack
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
	val catSoundVariants = registerGenerator<CatSoundVariant>()
	val catVariants = registerGenerator<CatVariant>()
	val chatTypes = registerGenerator<ChatType>()
	val chickenSoundVariants = registerGenerator<ChickenSoundVariant>()
	val chickenVariants = registerGenerator<ChickenVariant>()
	val configuredCarvers = registerGenerator<ConfiguredCarver>()
	val configuredFeatures = registerGenerator<ConfiguredFeature>()
	val cowSoundVariants = registerGenerator<CowSoundVariant>()
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
	val pigSoundVariants = registerGenerator<PigSoundVariant>()
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
	val timelines = registerGenerator<Timeline>()
	val tradeSets = registerGenerator<TradeSet>()
	val trimMaterials = registerGenerator<TrimMaterial>()
	val trimPatterns = registerGenerator<TrimPattern>()
	val villagerTrades = registerGenerator<VillagerTrade>()
	val wolfSoundVariants = registerGenerator<WolfSoundVariant>()
	val wolfVariants = registerGenerator<WolfVariant>()
	val worldClocks = registerGenerator<WorldClock>()
	val worldPresets = registerGenerator<WorldPreset>()
	val zombieNautilusVariants = registerGenerator<ZombieNautilusVariant>()

	var configuration = Configuration.DEFAULT
	var features: Features? = null
	var filter: Filter? = null
	var overlays: PackOverlays? = null
	var generated = false
		internal set
	var iconPath: Path? = null
	var path = Path("out")
	val pack = PackSection(
		description = textComponent("Generated by Kore"),
		minFormat = DEFAULT_PACK_FORMAT,
		maxFormat = DEFAULT_PACK_FORMAT,
		packFormat = DEFAULT_PACK_FORMAT
	)

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
	fun generatePackMCMetaFile() = jsonEncoder.encodeToString(PackMCMeta(pack, overlays, features, filter))

	/** Checks if the datapack is compatible with the other pack by comparing pack format ranges. */
	fun isCompatibleWith(otherPack: PackMCMeta): Boolean {
		if (pack.isCompatibleWith(otherPack.pack)) return true

		val packFormatPrint = "Format range: current: ${pack.formatRangeString()} other: ${otherPack.pack.formatRangeString()}."
		warn("The pack format range of the other pack is different from the current one. This may cause issues.")
		warn(packFormatPrint)
		return false
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

/** Sets the icon path of the pack displayed in-game. */
fun DataPack.iconPath(path: String) {
	iconPath = Path(path)
}

/** Sets the icon path of the pack displayed in-game. */
fun DataPack.iconPath(path: Path) {
	iconPath = path
}

/** Sets the output path of the pack. */
fun DataPack.path(path: String) {
	this.path = Path(path)
}

/** Sets the output path of the pack. */
fun DataPack.path(path: Path) {
	this.path = path
}

/**
 * Exports the whole datapack as an in-memory map of relative path (`data/...` or `pack.mcmeta`) to file content.
 *
 * This is the platform-neutral distribution path (works on JS): it never touches the filesystem, so the host
 * is free to write the returned entries however it wants. For on-disk/archive generation on the JVM, use
 * [generate]/[generateZip]/[generateJar] instead.
 */
fun DataPack.exportAsStrings(): Map<String, String> = buildMap {
	put("pack.mcmeta", generatePackMCMetaFile())

	(functions + generatedFunctions).distinctBy { it.getFinalPath() }.forEach {
		put(it.getFinalPath(), it.lines.joinToString("\n"))
	}

	generators.flatten().forEach {
		put(
			it.getPathFromDataDir(Path("data"), it.namespace ?: name).asInvariantPathSeparator,
			it.generateJson(this@exportAsStrings)
		)
	}
}