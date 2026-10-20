package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `spring_feature` feature.
 *
 * Places a single fluid source block in the wall of a cave or a cliff, flowing out into the open space next to it.
 * Vanilla places its water and lava springs with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spring_feature
 *
 * @property state The fluid placed, as a block state, with `falling` set to `true` in vanilla's springs.
 * @property rockCount Number of adjacent blocks, the one above not counted, that must be in [validBlocks], `4` in
 * vanilla's springs.
 * @property holeCount Number of adjacent air blocks required, the one above not counted, `1` in vanilla's springs.
 * @property requiresBlockBelow Whether a block of [validBlocks] is required below, `true` in vanilla's springs.
 * @property validBlocks A block, a block tag or a list of blocks the spring can be built against.
 */
@Serializable
data class SpringFeature(
	var state: BlockState = blockStateStone(),
	var rockCount: Int = 0,
	var holeCount: Int = 0,
	var requiresBlockBelow: Boolean = false,
	var validBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

/**
 * Creates a `spring_feature` configured feature, configured in [block].
 *
 * ```kotlin
 * springFeature("water_spring") {
 *     state = blockState(Blocks.WATER, "falling" to "true")
 *     rockCount = 4
 *     holeCount = 1
 *     requiresBlockBelow = true
 *     validBlocks = listOf(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spring_feature
 */
fun ConfiguredFeatures.springFeature(fileName: String, block: SpringFeature.() -> Unit = {}) =
	dp.configuredFeature(fileName, SpringFeature().apply(block))
