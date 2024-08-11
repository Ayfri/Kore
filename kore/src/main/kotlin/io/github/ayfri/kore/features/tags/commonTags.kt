package io.github.ayfri.kore.features.tags

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.tagged.*

fun DataPack.bannerPatternTag(
	fileName: String = "banner_pattern",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BannerPatternTagArgument>.() -> Unit = {},
) = tag(fileName, "banner_pattern", namespace, replace, block)

fun DataPack.biomeTag(
	fileName: String = "biome",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BiomeTagArgument>.() -> Unit = {},
) = tag(fileName, "biome", namespace, replace, block)

fun DataPack.blockTag(
	fileName: String = "block",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<BlockTagArgument>.() -> Unit = {},
) = tag(fileName, "block", namespace, replace, block)

fun DataPack.catVariantTag(
	fileName: String = "cat_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<CatVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "cat_variant", namespace, replace, block)

fun DataPack.damageTypeTag(
	fileName: String = "damage_type",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<DamageTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "damage_type", namespace, replace, block)

fun DataPack.entityTypeTag(
	fileName: String = "entity_type",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<EntityTypeTagArgument>.() -> Unit = {},
) = tag(fileName, "entity_type", namespace, replace, block)

fun DataPack.flatLevelGeneratorPresetTag(
	fileName: String = "flat_level_generator_preset",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FlatLevelGeneratorPresetTagArgument>.() -> Unit = {},
) = tag(fileName, "flat_level_generator_preset", namespace, replace, block)

fun DataPack.fluidTag(
	fileName: String = "fluid",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FluidTagArgument>.() -> Unit = {},
) = tag(fileName, "fluid", namespace, replace, block)

fun DataPack.functionTag(
	fileName: String = "function",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<FunctionTagArgument>.() -> Unit = {},
) = tag(fileName, "function", namespace, replace, block)

fun DataPack.gameEventTag(
	fileName: String = "game_event",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<GameEventTagArgument>.() -> Unit = {},
) = tag(fileName, "game_event", namespace, replace, block)

fun DataPack.instrumentTag(
	fileName: String = "instrument",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<InstrumentTagArgument>.() -> Unit = {},
) = tag(fileName, "instrument", namespace, replace, block)

fun DataPack.itemTag(
	fileName: String = "item",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<ItemTagArgument>.() -> Unit = {},
) = tag(fileName, "item", namespace, replace, block)

fun DataPack.paintingVariantTag(
	fileName: String = "painting_variant",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PaintingVariantTagArgument>.() -> Unit = {},
) = tag(fileName, "painting_variant", namespace, replace, block)

fun DataPack.pointOfInterestTypeTag(
	fileName: String = "point_of_interest_type",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<PointOfInterestTagArgument>.() -> Unit = {},
) = tag(fileName, "point_of_interest_type", namespace, replace, block)

fun DataPack.structureTag(
	fileName: String = "structure",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<StructureTagArgument>.() -> Unit = {},
) = tag(fileName, "structure", namespace, replace, block)

fun DataPack.worldPresetTag(
	fileName: String = "world_preset",
	namespace: String = name,
	replace: Boolean = false,
	block: Tag<WorldPresetTagArgument>.() -> Unit = {},
) = tag(fileName, "world_preset", namespace, replace, block)
