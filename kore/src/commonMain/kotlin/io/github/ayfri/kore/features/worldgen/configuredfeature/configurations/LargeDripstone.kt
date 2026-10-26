package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
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
 * Configuration for the `large_dripstone` feature.
 *
 * Grows a large dripstone column in a cave, a stalactite, a stalagmite or both joined into one pillar, between the
 * floor and the ceiling found around the placement position. Vanilla grows the big dripstones of the dripstone caves
 * with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#large_dripstone
 *
 * @property floorToCeilingSearchRange Distance searched from the placement position to find the cave floor and ceiling,
 * `1` to `512`, `30` in vanilla when `null`.
 * @property columnRadius Radius of the columns, `0` to `16`.
 * @property heightScale Scale of the column height, `0` to `20`, higher grows taller columns.
 * @property maxColumnRadiusToCaveHeightRatio Ratio of the largest column radius to the cave height, `0` to `1`.
 * @property stalactiteBluntness How much the tip of a stalactite is truncated, `0.1` to `10`, higher gives blunter and
 * shorter ones.
 * @property stalagmiteBluntness How much the tip of a stalagmite is truncated, `0.1` to `10`, higher gives blunter and
 * shorter ones.
 * @property windSpeed How much the columns lean, `0` to `2`.
 * @property minRadiusForWind Smallest column radius the wind leans, `0` to `100`.
 * @property minBluntnessForWind Smallest bluntness the wind leans.
 * @property replaceableBlocks A block, a block tag or a list of blocks the columns can replace.
 */
@Serializable
data class LargeDripstone(
	var floorToCeilingSearchRange: Int? = null,
	var columnRadius: IntProvider = ConstantIntProvider(0),
	var heightScale: FloatProvider = ConstantFloatProvider(0f),
	var maxColumnRadiusToCaveHeightRatio: Float = 0f,
	var stalactiteBluntness: FloatProvider = ConstantFloatProvider(0f),
	var stalagmiteBluntness: FloatProvider = ConstantFloatProvider(0f),
	var windSpeed: FloatProvider = ConstantFloatProvider(0f),
	var minRadiusForWind: Int = 0,
	var minBluntnessForWind: Float = 0f,
	var replaceableBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig(), FloatProviderScope, IntProviderScope

/**
 * Creates a `large_dripstone` configured feature, configured in [block].
 *
 * The int provider and float provider builders are scoped to [block].
 *
 * ```kotlin
 * largeDripstone("big_dripstone") {
 *     columnRadius = clamped(3, 16, uniform(3, 19))
 *     heightScale = uniform(0.4f, 2f)
 *     maxColumnRadiusToCaveHeightRatio = 0.33f
 *     stalactiteBluntness = uniform(0.3f, 0.9f)
 *     stalagmiteBluntness = uniform(0.4f, 1f)
 *     windSpeed = uniform(0f, 0.3f)
 *     minRadiusForWind = 4
 *     minBluntnessForWind = 0.6f
 *     replaceableBlocks = listOf(Tags.Block.DRIPSTONE_REPLACEABLE_BLOCKS)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#large_dripstone
 */
fun ConfiguredFeatures.largeDripstone(fileName: String, block: LargeDripstone.() -> Unit = {}) =
	dp.configuredFeature(fileName, LargeDripstone().apply(block))

/**
 * Sets [LargeDripstone.columnRadius] to [value], the radius of the column.
 *
 * ```kotlin
 * columnRadius(clamped(3, 16, uniform(3, 19)))
 * ```
 */
fun LargeDripstone.columnRadius(value: IntProvider) {
	columnRadius = value
}

/** Sets [LargeDripstone.columnRadius] to the constant [value]. */
fun LargeDripstone.columnRadius(value: Int) = columnRadius(ConstantIntProvider(value))

/**
 * Sets [LargeDripstone.heightScale] to [value], the factor scaling the height of the dripstone.
 *
 * ```kotlin
 * heightScale(uniform(0.4f, 2f))
 * ```
 */
fun LargeDripstone.heightScale(value: FloatProvider) {
	heightScale = value
}

/** Sets [LargeDripstone.heightScale] to the constant [value]. */
fun LargeDripstone.heightScale(value: Float) = heightScale(ConstantFloatProvider(value))

/**
 * Sets [LargeDripstone.stalactiteBluntness] to [value], how blunt the stalactite tip is.
 *
 * ```kotlin
 * stalactiteBluntness(uniform(0.3f, 0.9f))
 * ```
 */
fun LargeDripstone.stalactiteBluntness(value: FloatProvider) {
	stalactiteBluntness = value
}

/** Sets [LargeDripstone.stalactiteBluntness] to the constant [value]. */
fun LargeDripstone.stalactiteBluntness(value: Float) = stalactiteBluntness(ConstantFloatProvider(value))

/**
 * Sets [LargeDripstone.stalagmiteBluntness] to [value], how blunt the stalagmite tip is.
 *
 * ```kotlin
 * stalagmiteBluntness(uniform(0.4f, 1f))
 * ```
 */
fun LargeDripstone.stalagmiteBluntness(value: FloatProvider) {
	stalagmiteBluntness = value
}

/** Sets [LargeDripstone.stalagmiteBluntness] to the constant [value]. */
fun LargeDripstone.stalagmiteBluntness(value: Float) = stalagmiteBluntness(ConstantFloatProvider(value))

/**
 * Sets [LargeDripstone.windSpeed] to [value], how strongly the wind bends the dripstone.
 *
 * ```kotlin
 * windSpeed(uniform(0f, 0.3f))
 * ```
 */
fun LargeDripstone.windSpeed(value: FloatProvider) {
	windSpeed = value
}

/** Sets [LargeDripstone.windSpeed] to the constant [value]. */
fun LargeDripstone.windSpeed(value: Float) = windSpeed(ConstantFloatProvider(value))
