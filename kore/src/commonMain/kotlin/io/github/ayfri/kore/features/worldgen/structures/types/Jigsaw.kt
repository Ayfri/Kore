package io.github.ayfri.kore.features.worldgen.structures.types

import io.github.ayfri.kore.features.worldgen.HeightMap
import io.github.ayfri.kore.features.worldgen.heightproviders.ConstantHeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProviderScope
import io.github.ayfri.kore.features.worldgen.structures.*
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.*
import io.github.ayfri.kore.features.worldgen.verticalanchors.Absolute
import io.github.ayfri.kore.generated.arguments.worldgen.BiomeOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredStructureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.TemplatePoolArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Assembles a structure out of template pool pieces, the only structure type whose shape comes from data.
 *
 * Generation starts by drawing a piece from [startPool], then repeatedly follows the jigsaw blocks of the pieces
 * already placed, each one naming the pool the next piece is drawn from, until [size] branching rounds are spent or no
 * piece fits any more. Villages, pillager outposts, bastions, ancient cities and trial chambers are all jigsaw
 * structures.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 *
 * @property startPool The template pool the first piece is drawn from.
 * @property size Between `1` and `20`, how many times pieces may branch out from the start piece.
 * @property startHeight The height the start piece is placed at, ignored when [projectStartToHeightmap] is set.
 * @property startJigsawName Only connect the start piece through the jigsaw blocks carrying this name.
 * @property projectStartToHeightmap Snaps the start piece onto a heightmap instead of using [startHeight].
 * @property maxDistanceFromCenter How far pieces may grow from the center, up to `128` blocks horizontally, or `116`
 * when [terrainAdaptation] is one of [TerrainAdaptation.BEARD_THIN], [TerrainAdaptation.BEARD_BOX] or
 * [TerrainAdaptation.BURY].
 * @property useExpansionHack The legacy village terrain hack, raising pieces above the ground.
 * @property poolAliases Per-instance pool rewiring, letting one structure produce differently themed variants.
 * @property dimensionPadding Blocks kept free above and below the structure, so it never touches the world bounds.
 * @property liquidSettings Whether the pieces waterlog when they land in water, `null` meaning apply waterlogging.
 */
@Serializable
data class Jigsaw(
	override var biomes: InlinableList<BiomeOrTagArgument> = emptyList(),
	override var step: GenerationStep = GenerationStep.SURFACE_STRUCTURES,
	override var spawnOverrides: SpawnOverrides = SpawnOverrides(),
	override var terrainAdaptation: TerrainAdaptation? = null,
	var startPool: TemplatePoolArgument,
	var size: Int = 1,
	var startHeight: HeightProvider = ConstantHeightProvider(Absolute(0)),
	var startJigsawName: String? = null,
	var projectStartToHeightmap: HeightMap? = null,
	var maxDistanceFromCenter: MaxDistanceFromCenter = MaxDistanceFromCenter(80),
	var useExpansionHack: Boolean = false,
	var poolAliases: List<PoolAlias>? = null,
	var dimensionPadding: DimensionPadding? = null,
	var liquidSettings: LiquidSettings? = null,
) : StructureType(), HeightProviderScope

/**
 * Creates a `jigsaw` structure growing from [startPool], configured in [init].
 *
 * The height provider builders are scoped to [init].
 *
 * ```kotlin
 * structures {
 *     jigsaw("my_village", startPool = villageStart) {
 *         biomes(Biomes.PLAINS)
 *         terrainAdaptation = TerrainAdaptation.BEARD_THIN
 *         size = 6
 *         startHeight = constantAbsolute(0)
 *         projectStartToHeightmap = HeightMap.WORLD_SURFACE_WG
 *         maxDistanceFromCenter(80)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/structure/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/structures
 * Minecraft Wiki: https://minecraft.wiki/w/Structure_definition
 */
fun StructuresScope.jigsaw(
	fileName: String = "jigsaw",
	startPool: TemplatePoolArgument,
	init: Jigsaw.() -> Unit = {},
): ConfiguredStructureArgument = dp.structure(fileName, Jigsaw(startPool = startPool).apply(init))

/**
 * Rewires the pools some aliases resolve to, drawn once per structure instance.
 *
 * ```kotlin
 * poolAliases {
 *     directPoolAlias(TemplatePools.Empty, housesPool)
 *
 *     randomPoolAlias(TemplatePools.Empty) {
 *         weightedPoolEntry(1, desertHouses)
 *         weightedPoolEntry(2, plainsHouses)
 *     }
 * }
 * ```
 */
fun Jigsaw.poolAliases(init: MutableList<PoolAlias>.() -> Unit = {}) {
	poolAliases = buildList(init)
}

/** Keeps [padding] blocks free both above and below the structure. */
fun Jigsaw.dimensionPadding(padding: Int) {
	dimensionPadding = DimensionPadding(padding)
}

/** Keeps [top] blocks free above the structure and [bottom] blocks below it. */
fun Jigsaw.dimensionPadding(top: Int, bottom: Int) {
	dimensionPadding = DimensionPadding(top, bottom)
}

/** Limits the structure to [distance] blocks from its center, in every direction. */
fun Jigsaw.maxDistanceFromCenter(distance: Int) {
	maxDistanceFromCenter = MaxDistanceFromCenter(distance)
}

/**
 * Limits the structure to [horizontal] blocks from its center horizontally, and [vertical] blocks vertically.
 *
 * [horizontal] caps at `128`, or `116` when the structure beards or buries its terrain.
 */
fun Jigsaw.maxDistanceFromCenter(horizontal: Int, vertical: Int? = null) {
	maxDistanceFromCenter = MaxDistanceFromCenter(horizontal, vertical)
}

/** Always resolves [alias] to [target]. */
fun MutableList<PoolAlias>.directPoolAlias(alias: TemplatePoolArgument, target: TemplatePoolArgument) =
	add(Direct(alias, target))

/** Resolves [alias] to one of [targets], drawn by weight. */
fun MutableList<PoolAlias>.randomPoolAlias(alias: TemplatePoolArgument, targets: List<WeightedPoolEntry> = emptyList()) =
	add(Random(alias, targets))

/** Resolves [alias] to one of the entries declared in [init], drawn by weight. */
fun MutableList<PoolAlias>.randomPoolAlias(
	alias: TemplatePoolArgument,
	init: MutableList<WeightedPoolEntry>.() -> Unit,
) = add(Random(alias, buildList(init)))

/** Draws one of [groups] by weight and applies every alias it holds at once, keeping a themed variant coherent. */
fun MutableList<PoolAlias>.randomGroupPoolAlias(groups: List<WeightedGroupEntry> = emptyList()) = add(RandomGroup(groups))

/** Draws one of the groups declared in [init] by weight and applies every alias it holds at once. */
fun MutableList<PoolAlias>.randomGroupPoolAlias(init: MutableList<WeightedGroupEntry>.() -> Unit) =
	add(RandomGroup(buildList(init)))

/** Adds a [data] pool candidate with a relative chance of [weight] to a [Random] alias. */
fun MutableList<WeightedPoolEntry>.weightedPoolEntry(weight: Int, data: TemplatePoolArgument) =
	add(WeightedPoolEntry(weight, data))

/** Adds a group of aliases, declared in [init], with a relative chance of [weight] to a [RandomGroup] alias. */
fun MutableList<WeightedGroupEntry>.weightedGroupEntry(weight: Int, init: MutableList<PoolAlias>.() -> Unit) =
	add(WeightedGroupEntry(weight, buildList(init)))
