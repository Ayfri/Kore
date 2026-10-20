package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `spike` feature.
 *
 * Grows a tall, thin spire of [state] up from the ground. Vanilla builds the packed ice spikes of the ice spikes biome
 * with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spike
 *
 * @property canPlaceOn The blocks the spike can grow on.
 * @property canReplace The blocks the spike can replace.
 * @property state The block state of the spike.
 */
@Serializable
data class Spike(
	var canPlaceOn: BlockPredicate = True,
	var canReplace: BlockPredicate = True,
	var state: BlockState = blockStateStone(),
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `spike` configured feature growing spires of [state], configured in [block].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * spike("ice_spike", state = blockState(Blocks.PACKED_ICE)) {
 *     canPlaceOn { matchingBlocks(Blocks.SNOW_BLOCK) }
 *     canReplace { matchingBlockTag(Tags.Block.ICE_SPIKE_REPLACEABLE) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spike
 */
fun ConfiguredFeatures.spike(
	fileName: String,
	state: BlockState = blockStateStone(),
	block: Spike.() -> Unit = {},
) = dp.configuredFeature(fileName, Spike(state = state).apply(block))

/**
 * Sets [Spike.canPlaceOn] to the predicate built in [block], the blocks the spike may grow on.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * spike("ice_spike", state = blockState(Blocks.PACKED_ICE)) {
 *     canPlaceOn { matchingBlocks(Blocks.SNOW_BLOCK) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Spike.canPlaceOn(block: BlockPredicatesScope.() -> Unit) {
	canPlaceOn = blockPredicate(block)
}

/**
 * Sets [Spike.canReplace] to the predicate built in [block], the blocks the spike may grow through.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * spike("ice_spike", state = blockState(Blocks.PACKED_ICE)) {
 *     canReplace { matchingBlockTag(Tags.Block.ICE_SPIKE_REPLACEABLE) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Spike.canReplace(block: BlockPredicatesScope.() -> Unit) {
	canReplace = blockPredicate(block)
}
