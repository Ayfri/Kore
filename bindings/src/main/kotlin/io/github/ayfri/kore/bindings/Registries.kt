package io.github.ayfri.kore.bindings

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

private val jsonDecoder = Json { ignoreUnknownKeys = true }

// Cache for valid registry types to avoid repeated processing
private var cachedValidRegistries: Map<String, String>? = null

/**
 * Downloads and caches the list of valid Minecraft registries from PixiGeko's repository.
 * Returns a map of datapack folder name to Kore Argument type name.
 */
private fun downloadAndCacheRegistries(): Map<String, String> {
	if (cachedValidRegistries != null) return cachedValidRegistries!!

	return try {
		val registriesUrl = urlGeneratedData("minecraft-generated/reports/datapack.json")
		val json = getFromCacheOrDownload("registries_$MINECRAFT_VERSION.json", registriesUrl)
		val jsonElement = jsonDecoder.parseToJsonElement(json)

		// The registries structure can vary; extract registry keys in a defensive way
		val registries = jsonElement.jsonObject["registries"]?.jsonObject?.keys?.associateWith { true }
			?: emptyMap<String, Boolean>()

		// Additional types that are valid but might not be in the standard registry
		val additionalTypes = mapOf(
			"minecraft:attribute_modifier" to true,
			"minecraft:consume_cooldown_group" to true,
			"minecraft:painting_asset" to true,
			"minecraft:trim_color_palette" to true,
			"minecraft:waypoint_style" to true,
			"minecraft:worldgen/configured_structure" to true,
		)

		// Merge additionalTypes into keys (we only need keys here)
		val allKeys = registries.keys + additionalTypes.keys

		// Map from registry names to their corresponding Kore Argument types
		val mapping = mutableMapOf<String, String>()
		allKeys.forEach { key ->
			val folderName = key.removePrefix("minecraft:").replace("/", "_")
			val argumentName = folderName.split("_", "/").joinToString("") { part ->
				part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
			} + "Argument"
			mapping[key.removePrefix("minecraft:")] = argumentName
		}

		cachedValidRegistries = mapping
		mapping
	} catch (e: Exception) {
		warn("[WARNING]: Could not download registry list, using fallback: ${e.message}")
		val fallback = getFallbackMapping()
		cachedValidRegistries = fallback
		fallback
	}
}

/**
 * Fallback mapping when download fails - based on known Kore argument types.
 */
private fun getFallbackMapping() = mapOf(
	"advancement" to "AdvancementArgument",
	"chat_type" to "ChatTypeArgument",
	"damage_type" to "DamageTypeArgument",
	"dialog" to "DialogArgument",
	"dimension" to "DimensionArgument",
	"dimension_type" to "DimensionTypeArgument",
	"enchantment" to "EnchantmentArgument",
	"item_modifier" to "ItemModifierArgument",
	"jukebox_song" to "JukeboxSongArgument",
	"loot_table" to "LootTableArgument",
	"painting_variant" to "PaintingVariantArgument",
	"predicate" to "PredicateArgument",
	"recipe" to "RecipeArgument",
	"trim_material" to "TrimMaterialArgument",
	"trim_pattern" to "TrimPatternArgument",
	"banner_pattern" to "BannerPatternArgument",
	"wolf_variant" to "WolfVariantArgument",
	// Worldgen types (with full paths as they appear in datapacks)
	"worldgen/biome" to "BiomeArgument",
	"worldgen/configured_carver" to "ConfiguredCarverArgument",
	"worldgen/configured_feature" to "ConfiguredFeatureArgument",
	"worldgen/density_function" to "DensityFunctionArgument",
	"worldgen/flat_level_generator_preset" to "FlatLevelGeneratorPresetArgument",
	"worldgen/noise" to "NoiseArgument",
	"worldgen/noise_settings" to "NoiseSettingsArgument",
	"worldgen/placed_feature" to "PlacedFeatureArgument",
	"worldgen/processor_list" to "ProcessorListArgument",
	"worldgen/structure" to "StructureArgument",
	"worldgen/structure_set" to "StructureSetArgument",
	"worldgen/template_pool" to "TemplatePoolArgument",
	"worldgen/world_preset" to "WorldPresetArgument",
)

// Mapping from datapack folder names to corresponding Kore Argument types
internal val DATAPACK_FOLDER_TO_ARGUMENT: Map<String, String> by lazy {
	val downloaded = downloadAndCacheRegistries()
	// Merge with tag types that may not be in the registry
	downloaded + TAG_TYPES
}

// Tag types mapping - these are always valid regardless of registry
private val TAG_TYPES = mapOf(
	"tags/advancement" to "TagArgument",
	"tags/attribute_modifier" to "TagArgument",
	"tags/banner_pattern" to "TagArgument",
	"tags/block" to "TagArgument",
	"tags/cat_variant" to "TagArgument",
	"tags/chicken_variant" to "TagArgument",
	"tags/cow_variant" to "TagArgument",
	"tags/damage_type" to "TagArgument",
	"tags/dialog" to "TagArgument",
	"tags/enchantment" to "TagArgument",
	"tags/enchantment_provider" to "TagArgument",
	"tags/entity_type" to "TagArgument",
	"tags/fluid" to "TagArgument",
	"tags/frog_variant" to "TagArgument",
	"tags/game_event" to "TagArgument",
	"tags/instrument" to "TagArgument",
	"tags/item" to "TagArgument",
	"tags/jukebox_song" to "TagArgument",
	"tags/painting_variant" to "TagArgument",
	"tags/pig_variant" to "TagArgument",
	"tags/point_of_interest_type" to "TagArgument",
	"tags/structure" to "TagArgument",
	"tags/trial_spawner" to "TagArgument",
	"tags/wolf_sound_variant" to "TagArgument",
	"tags/wolf_variant" to "TagArgument",
	"tags/worldgen/biome" to "TagArgument",
	"tags/worldgen/configured_carver" to "TagArgument",
	"tags/worldgen/configured_feature" to "TagArgument",
	"tags/worldgen/density_function" to "TagArgument",
	"tags/worldgen/flat_level_generator_preset" to "TagArgument",
	"tags/worldgen/noise" to "TagArgument",
	"tags/worldgen/noise_settings" to "TagArgument",
	"tags/worldgen/placed_feature" to "TagArgument",
	"tags/worldgen/processor_list" to "TagArgument",
	"tags/worldgen/structure" to "TagArgument",
	"tags/worldgen/structure_set" to "TagArgument",
	"tags/worldgen/template_pool" to "TagArgument",
	"tags/worldgen/world_preset" to "TagArgument",
)

/**
 * Checks if a datapack folder type has a corresponding Kore Argument interface.
 * This prevents generating invalid code for unsupported resource types.
 */
fun isValidResourceType(resourceType: String) = resourceType in DATAPACK_FOLDER_TO_ARGUMENT
