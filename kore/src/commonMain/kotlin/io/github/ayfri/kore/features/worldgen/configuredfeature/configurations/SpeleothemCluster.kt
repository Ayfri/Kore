package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `speleothem_cluster` feature.
 *
 * Grows a cluster of stalagmites and stalactites between the floor and the ceiling of a cave. Vanilla grows the
 * dripstone clusters of the dripstone caves and the sulfur spike clusters with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#speleothem_cluster
 *
 * @property floorToCeilingSearchRange Distance searched for the floor and the ceiling, `1` to `512`.
 * @property height Height of the speleothems, up to `128`.
 * @property radius Radius of the cluster, up to `128`.
 * @property maxStalagmiteStalactiteHeightDiff Maximum height difference between a stalagmite and a stalactite, `0` to
 * `64`.
 * @property heightDeviation Deviation of the speleothem height, `1` to `64`.
 * @property speleothemBlockLayerThickness Thickness of the layer of base blocks, up to `128`.
 * @property density Density of the speleothems, `0` to `2`.
 * @property wetness Wetness of the cluster, `0` to `2`.
 * @property chanceOfSpeleothemAtMaxDistanceFromCenter Chance, `0` to `1`, of a speleothem at the maximum distance from
 * the center.
 * @property maxDistanceFromEdgeAffectingChanceOfSpeleothem Distance from the edge over which the chance of a speleothem
 * changes, `1` to `64`.
 * @property maxDistanceFromCenterAffectingHeightBias Distance from the center over which the height bias changes, `1`
 * to `64`.
 * @property baseBlock The block state of the base of the speleothems.
 * @property pointedBlock The block state of the pointed part of the speleothems.
 * @property replaceableBlocks A block, a block tag or a list of blocks the cluster can replace.
 */
@Serializable
data class SpeleothemCluster(
	var floorToCeilingSearchRange: Int = 0,
	var height: IntProvider = ConstantIntProvider(0),
	var radius: IntProvider = ConstantIntProvider(0),
	var maxStalagmiteStalactiteHeightDiff: Int = 0,
	var heightDeviation: Int = 0,
	var speleothemBlockLayerThickness: IntProvider = ConstantIntProvider(0),
	var density: IntProvider = ConstantIntProvider(0),
	var wetness: IntProvider = ConstantIntProvider(0),
	var chanceOfSpeleothemAtMaxDistanceFromCenter: Int = 0,
	var maxDistanceFromEdgeAffectingChanceOfSpeleothem: Int = 0,
	var maxDistanceFromCenterAffectingHeightBias: Int = 0,
	var baseBlock: BlockState = blockStateStone(),
	var pointedBlock: BlockState = blockStateStone(),
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig(), IntProviderScope

/**
 * Creates a `speleothem_cluster` configured feature, configured in [block].
 *
 * The int provider builders are scoped to [block].
 *
 * ```kotlin
 * speleothemCluster("dripstone_cluster") {
 *     floorToCeilingSearchRange = 12
 *     height = uniform(3, 6)
 *     radius = uniform(2, 8)
 *     maxStalagmiteStalactiteHeightDiff = 1
 *     heightDeviation = 3
 *     speleothemBlockLayerThickness = uniform(2, 4)
 *     maxDistanceFromEdgeAffectingChanceOfSpeleothem = 3
 *     maxDistanceFromCenterAffectingHeightBias = 8
 *     baseBlock = blockState(Blocks.DRIPSTONE_BLOCK)
 *     pointedBlock = blockState(Blocks.POINTED_DRIPSTONE)
 *     replaceableBlocks = listOf(Tags.Block.DRIPSTONE_REPLACEABLE_BLOCKS)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#speleothem_cluster
 */
fun ConfiguredFeatures.speleothemCluster(fileName: String, block: SpeleothemCluster.() -> Unit = {}) =
	dp.configuredFeature(fileName, SpeleothemCluster().apply(block))
