package io.github.ayfri.kore.features.tags

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.FunctionTagArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.generated.arguments.tagged.*
import io.github.ayfri.kore.generated.arguments.worldgen.tagged.*

/** Create a banner pattern tag. Produces `data/<namespace>/tags/banner_pattern/<fileName>.json`. */
fun DataPack.bannerPatternTag(
	fileName: String = "banner_pattern",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BannerPatternTagArgument>.() -> Unit = {},
) = tag(fileName, "banner_pattern", namespace, replace, block)

/** Create a biome tag. Produces `data/<namespace>/tags/worldgen/biome/<fileName>.json`. */
fun DataPack.biomeTag(
	fileName: String = "biome",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BiomeTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/biome", namespace, replace, block)

/** Create a block tag. Produces `data/<namespace>/tags/block/<fileName>.json`. */
fun DataPack.blockTag(
	fileName: String = "block",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BlockTagArgument>.() -> Unit = {},
) = tag(fileName, "block", namespace, replace, block)

/** Create a cat variant tag. Produces `data/<namespace>/tags/cat_variant/<fileName>.json`. */
fun DataPack.catVariantTag(
	fileName: String = "cat_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<CatVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "cat_variant", namespace, replace, block)

/** Create a configured carver tag. Produces `data/<namespace>/tags/worldgen/configured_carver/<fileName>.json`. */
fun DataPack.configuredCarverTag(
	fileName: String = "configured_carver",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ConfiguredCarverTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/configured_carver", namespace, replace, block)

/** Create a configured feature tag. Produces `data/<namespace>/tags/worldgen/configured_feature/<fileName>.json`. */
fun DataPack.configuredFeatureTag(
	fileName: String = "configured_feature",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ConfiguredFeatureTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/configured_feature", namespace, replace, block)

/** Create a configured structure feature tag. Produces `data/<namespace>/tags/worldgen/structure/<fileName>.json`. */
fun DataPack.configuredStructureTag(
	fileName: String = "configured_structure_feature",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ConfiguredStructureTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/structure", namespace, replace, block)

/** Create a damage type tag. Produces `data/<namespace>/tags/damage_type/<fileName>.json`. */
fun DataPack.damageTypeTag(
	fileName: String = "damage_type",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<DamageTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "damage_type", namespace, replace, block)

/** Create an enchantment tag. Produces `data/<namespace>/tags/enchantment/<fileName>.json`. */
fun DataPack.enchantmentTag(
	fileName: String = "enchantment",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<EnchantmentTagArgument>.() -> Unit = {},
) = tag(fileName, "enchantment", namespace, replace, block)

/** Create an entity type tag. Produces `data/<namespace>/tags/entity_type/<fileName>.json`. */
fun DataPack.entityTypeTag(
	fileName: String = "entity_type",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<EntityTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "entity_type", namespace, replace, block)

/** Create a flat level generator preset tag. Produces `data/<namespace>/tags/worldgen/flat_level_generator_preset/<fileName>.json`. */
fun DataPack.flatLevelGeneratorPresetTag(
	fileName: String = "flat_level_generator_preset",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FlatLevelGeneratorPresetTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/flat_level_generator_preset", namespace, replace, block)

/** Create a noise tag. Produces `data/<namespace>/tags/worldgen/noise/<fileName>.json`. */
fun DataPack.noiseTag(
	fileName: String = "noise",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<NoiseTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/noise", namespace, replace, block)

/** Create a noise settings tag. Produces `data/<namespace>/tags/worldgen/noise_settings/<fileName>.json`. */
fun DataPack.noiseSettingsTag(
	fileName: String = "noise_settings",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<NoiseSettingsTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/noise_settings", namespace, replace, block)

/** Create a placed feature tag. Produces `data/<namespace>/tags/worldgen/placed_feature/<fileName>.json`. */
fun DataPack.placedFeatureTag(
	fileName: String = "placed_feature",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PlacedFeatureTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/placed_feature", namespace, replace, block)

/** Create a processor list tag. Produces `data/<namespace>/tags/worldgen/processor_list/<fileName>.json`. */
fun DataPack.processorListTag(
	fileName: String = "processor_list",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ProcessorListTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/processor_list", namespace, replace, block)

/** Create a template pool tag. Produces `data/<namespace>/tags/worldgen/template_pool/<fileName>.json`. */
fun DataPack.templatePoolTag(
	fileName: String = "template_pool",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<TemplatePoolTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/template_pool", namespace, replace, block)

/** Create a fluid tag. Produces `data/<namespace>/tags/fluid/<fileName>.json`. */
fun DataPack.fluidTag(
	fileName: String = "fluid",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FluidTagArgument>.() -> Unit = {},
) = tag(fileName, "fluid", namespace, replace, block)

/** Create a frog variant tag. Produces `data/<namespace>/tags/frog_variant/<fileName>.json`. */
fun DataPack.frogVariantTag(
	fileName: String = "frog_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FrogVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "frog_variant", namespace, replace, block)

/** Create a function tag. Produces `data/<namespace>/tags/function/<fileName>.json`. */
fun DataPack.functionTag(
	fileName: String = "function",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FunctionTagArgument>.() -> Unit = {},
) = tag(fileName, "function", namespace, replace, block)

/** Create a game event tag. Produces `data/<namespace>/tags/game_event/<fileName>.json`. */
fun DataPack.gameEventTag(
	fileName: String = "game_event",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<GameEventTagArgument>.() -> Unit = {},
) = tag(fileName, "game_event", namespace, replace, block)

/** Create an instrument tag. Produces `data/<namespace>/tags/instrument/<fileName>.json`. */
fun DataPack.instrumentTag(
	fileName: String = "instrument",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<InstrumentTagArgument>.() -> Unit = {},
) = tag(fileName, "instrument", namespace, replace, block)

/** Create an item tag. Produces `data/<namespace>/tags/item/<fileName>.json`. */
fun DataPack.itemTag(
	fileName: String = "item",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ItemTagArgument>.() -> Unit = {},
) = tag(fileName, "item", namespace, replace, block)

/** Create a painting variant tag. Produces `data/<namespace>/tags/painting_variant/<fileName>.json`. */
fun DataPack.paintingVariantTag(
	fileName: String = "painting_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PaintingVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "painting_variant", namespace, replace, block)

/** Create a point of interest type tag. Produces `data/<namespace>/tags/point_of_interest_type/<fileName>.json`. */
fun DataPack.pointOfInterestTypeTag(
	fileName: String = "point_of_interest_type",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PointOfInterestTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "point_of_interest_type", namespace, replace, block)

/** Create a structure tag. Produces `data/<namespace>/tags/structure/<fileName>.json`. */
fun DataPack.structureTag(
	fileName: String = "structure",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<StructureTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/structure", namespace, replace, block)

/** Create a trim material tag. Produces `data/<namespace>/tags/trim_material/<fileName>.json`. */
fun DataPack.trimMaterialTag(
	fileName: String = "trim_material",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<TrimMaterialTagArgument>.() -> Unit = {},
) = tag(fileName, "trim_material", namespace, replace, block)

/** Create a trim pattern tag. Produces `data/<namespace>/tags/trim_pattern/<fileName>.json`. */
fun DataPack.trimPatternTag(
	fileName: String = "trim_pattern",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<TrimPatternTagArgument>.() -> Unit = {},
) = tag(fileName, "trim_pattern", namespace, replace, block)

/** Create a pig variant tag. Produces `data/<namespace>/tags/pig_variant/<fileName>.json`. */
fun DataPack.pigVariantTag(
	fileName: String = "pig_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PigVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "pig_variant", namespace, replace, block)

/** Create a wolf variant tag. Produces `data/<namespace>/tags/wolf_variant/<fileName>.json`. */
fun DataPack.wolfVariantTag(
	fileName: String = "wolf_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<WolfVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "wolf_variant", namespace, replace, block)

/** Create a world preset tag. Produces `data/<namespace>/tags/world_preset/<fileName>.json`. */
fun DataPack.worldPresetTag(
	fileName: String = "world_preset",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<WorldPresetTagArgument>.() -> Unit = {},
) = tag(fileName, "worldgen/world_preset", namespace, replace, block)
