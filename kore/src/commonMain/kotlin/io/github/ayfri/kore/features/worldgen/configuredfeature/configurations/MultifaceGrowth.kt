package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `multiface_growth` feature.
 *
 * Grows a multiface block, glow lichen or sculk vein, over the faces of nearby blocks. Vanilla uses it for the glow
 * lichen of the caves and for the sculk veins of the deep dark.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#multiface_growth
 *
 * @property block The multiface block that grows, `glow_lichen` or `sculk_vein`.
 * @property searchRange Range around the placement position in which a surface to grow on is searched, `1` to `64`,
 * `10` in vanilla when `null`.
 * @property chanceOfSpreading Chance, `0` to `1`, that the growth spreads onto the neighbouring surfaces, `0.5` in
 * vanilla when `null`.
 * @property canPlaceOnFloor Whether the growth can cover floors, `false` in vanilla when `null`.
 * @property canPlaceOnCeiling Whether the growth can cover ceilings, `false` in vanilla when `null`.
 * @property canPlaceOnWall Whether the growth can cover walls, `false` in vanilla when `null`.
 * @property canBePlacedOn A block, a block tag or a list of blocks the growth can attach to.
 */
@Serializable
data class MultifaceGrowth(
	var block: BlockArgument,
	var searchRange: Int? = null,
	var chanceOfSpreading: Double? = null,
	var canPlaceOnFloor: Boolean? = null,
	var canPlaceOnCeiling: Boolean? = null,
	var canPlaceOnWall: Boolean? = null,
	var canBePlacedOn: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

/**
 * Creates a `multiface_growth` configured feature growing [block], configured in [init].
 *
 * ```kotlin
 * multifaceGrowth("cave_lichen", Blocks.GLOW_LICHEN) {
 *     searchRange = 20
 *     canPlaceOnCeiling = true
 *     canPlaceOnWall = true
 *     canBePlacedOn = listOf(Blocks.STONE, Blocks.TUFF, Blocks.DEEPSLATE)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#multiface_growth
 */
fun ConfiguredFeatures.multifaceGrowth(
	fileName: String,
	block: BlockArgument,
	init: MultifaceGrowth.() -> Unit = {},
) = dp.configuredFeature(fileName, MultifaceGrowth(block).apply(init))
