package features.tags

import DataPack
import arguments.types.resources.tagged.*

fun DataPack.bannerPatternTag(
	fileName: String = "banner_patterns",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BannerPatternTagArgument>.() -> Unit = {},
) = tag(fileName, "banner_patterns", namespace, replace, block)

fun DataPack.biomeTag(
	fileName: String = "biomes",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BiomeTagArgument>.() -> Unit = {},
) = tag(fileName, "biomes", namespace, replace, block)

fun DataPack.blockTag(
	fileName: String = "blocks",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BlockTagArgument>.() -> Unit = {},
) = tag(fileName, "blocks", namespace, replace, block)

fun DataPack.catVariantTag(
	fileName: String = "cat_variants",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<CatVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "cat_variants", namespace, replace, block)

fun DataPack.damageTypeTag(
	fileName: String = "damage_types",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<DamageTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "damage_types", namespace, replace, block)

fun DataPack.entityTypeTag(
	fileName: String = "entity_types",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<EntityTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "entity_types", namespace, replace, block)

fun DataPack.flatLevelGeneratorPresetTag(
	fileName: String = "flat_level_generator_presets",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FlatLevelGeneratorPresetTagArgument>.() -> Unit = {},
) = tag(fileName, "flat_level_generator_presets", namespace, replace, block)

fun DataPack.fluidTag(
	fileName: String = "fluids",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FluidTagArgument>.() -> Unit = {},
) = tag(fileName, "fluids", namespace, replace, block)

fun DataPack.functionTag(
	fileName: String = "functions",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FunctionTagArgument>.() -> Unit = {},
) = tag(fileName, "functions", namespace, replace, block)

fun DataPack.instrumentTag(
	fileName: String = "instruments",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<InstrumentTagArgument>.() -> Unit = {},
) = tag(fileName, "instruments", namespace, replace, block)

fun DataPack.itemTag(
	fileName: String = "items",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ItemTagArgument>.() -> Unit = {},
) = tag(fileName, "items", namespace, replace, block)

fun DataPack.gameEventTag(
	fileName: String = "game_events",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<GameEventTagArgument>.() -> Unit = {},
) = tag(fileName, "game_events", namespace, replace, block)

fun DataPack.paintingVariantTag(
	fileName: String = "painting_variants",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PaintingVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "painting_variants", namespace, replace, block)

fun DataPack.pointOfInterestTypeTag(
	fileName: String = "point_of_interest_types",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PointOfInterestTagArgument>.() -> Unit = {},
) = tag(fileName, "point_of_interest_types", namespace, replace, block)

fun DataPack.structureTag(
	fileName: String = "structures",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<StructureTagArgument>.() -> Unit = {},
) = tag(fileName, "structures", namespace, replace, block)

fun DataPack.worldPresetTag(
	fileName: String = "world_presets",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<WorldPresetTagArgument>.() -> Unit = {},
) = tag(fileName, "world_presets", namespace, replace, block)
