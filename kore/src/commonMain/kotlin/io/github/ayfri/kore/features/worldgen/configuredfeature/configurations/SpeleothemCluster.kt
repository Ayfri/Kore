package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.floatproviders.ConstantFloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProviderScope
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
 * @property density Float provider of the density of the speleothems, `0` to `2`.
 * @property wetness Float provider of the wetness of the cluster, `0` to `2`.
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
	var floorToCeilingSearchRange: Int = 1,
	var height: IntProvider = ConstantIntProvider(0),
	var radius: IntProvider = ConstantIntProvider(0),
	var maxStalagmiteStalactiteHeightDiff: Int = 0,
	var heightDeviation: Int = 1,
	var speleothemBlockLayerThickness: IntProvider = ConstantIntProvider(0),
	var density: FloatProvider = ConstantFloatProvider(0f),
	var wetness: FloatProvider = ConstantFloatProvider(0f),
	var chanceOfSpeleothemAtMaxDistanceFromCenter: Float = 0f,
	var maxDistanceFromEdgeAffectingChanceOfSpeleothem: Int = 1,
	var maxDistanceFromCenterAffectingHeightBias: Int = 1,
	var baseBlock: BlockState = blockStateStone(),
	var pointedBlock: BlockState = blockStateStone(),
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig(), FloatProviderScope, IntProviderScope

/**
 * Creates a `speleothem_cluster` configured feature, configured in [block].
 *
 * The int provider and float provider builders are scoped to [block].
 *
 * ```kotlin
 * speleothemCluster("dripstone_cluster") {
 *     floorToCeilingSearchRange = 12
 *     height = uniform(3, 6)
 *     radius = uniform(2, 8)
 *     maxStalagmiteStalactiteHeightDiff = 1
 *     heightDeviation = 3
 *     speleothemBlockLayerThickness = uniform(2, 4)
 *     density = uniform(0.3f, 0.7f)
 *     wetness = clampedNormal(mean = 0.1f, deviation = 0.3f, min = 0.1f, max = 0.9f)
 *     chanceOfSpeleothemAtMaxDistanceFromCenter = 0.1f
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

/**
 * Sets [SpeleothemCluster.density] to [value], the chance of a speleothem growing on a column.
 *
 * ```kotlin
 * density(uniform(0.3f, 0.7f))
 * ```
 */
fun SpeleothemCluster.density(value: FloatProvider) {
	density = value
}

/** Sets [SpeleothemCluster.density] to the constant [value]. */
fun SpeleothemCluster.density(value: Float) = density(ConstantFloatProvider(value))

/**
 * Sets [SpeleothemCluster.height] to [value], the height of the cluster.
 *
 * ```kotlin
 * height(uniform(3, 6))
 * ```
 */
fun SpeleothemCluster.height(value: IntProvider) {
	height = value
}

/** Sets [SpeleothemCluster.height] to the constant [value]. */
fun SpeleothemCluster.height(value: Int) = height(ConstantIntProvider(value))

/**
 * Sets [SpeleothemCluster.radius] to [value], the radius of the cluster.
 *
 * ```kotlin
 * radius(uniform(2, 8))
 * ```
 */
fun SpeleothemCluster.radius(value: IntProvider) {
	radius = value
}

/** Sets [SpeleothemCluster.radius] to the constant [value]. */
fun SpeleothemCluster.radius(value: Int) = radius(ConstantIntProvider(value))

/**
 * Sets [SpeleothemCluster.speleothemBlockLayerThickness] to [value], the thickness of the base block layer.
 *
 * ```kotlin
 * speleothemBlockLayerThickness(uniform(2, 4))
 * ```
 */
fun SpeleothemCluster.speleothemBlockLayerThickness(value: IntProvider) {
	speleothemBlockLayerThickness = value
}

/** Sets [SpeleothemCluster.speleothemBlockLayerThickness] to the constant [value]. */
fun SpeleothemCluster.speleothemBlockLayerThickness(value: Int) =
	speleothemBlockLayerThickness(ConstantIntProvider(value))

/**
 * Sets [SpeleothemCluster.wetness] to [value], how likely a speleothem is wet.
 *
 * ```kotlin
 * wetness(clampedNormal(mean = 0.1f, deviation = 0.3f, min = 0.1f, max = 0.9f))
 * ```
 */
fun SpeleothemCluster.wetness(value: FloatProvider) {
	wetness = value
}

/** Sets [SpeleothemCluster.wetness] to the constant [value]. */
fun SpeleothemCluster.wetness(value: Float) = wetness(ConstantFloatProvider(value))
