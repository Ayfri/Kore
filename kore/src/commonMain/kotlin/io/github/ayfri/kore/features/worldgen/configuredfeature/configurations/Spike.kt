package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `spike` feature.
 *
 * Generates tall, spiky columns of a given block. The block material, the surface it can appear on,
 * and the blocks it can replace are all configurable.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spike
 */
@Serializable
data class Spike(
	var canPlaceOn: BlockPredicate = True,
	var canReplace: BlockPredicate = True,
	var state: BlockState = blockStateStone(),
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `spike` configured feature, placing spiky columns of [state].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * spike("ice_spike", state = blockState(Blocks.PACKED_ICE)) {
 *     canPlaceOn { matchingBlockTag(Tags.Block.SNOW) }
 *     canReplace { replaceable() }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spike
 */
fun ConfiguredFeatures.spike(
	fileName: String,
	state: BlockState = blockStateStone(),
	block: Spike.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Spike(state = state).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Sets [Spike.canPlaceOn] to the predicate built in [block], the blocks the spike may grow on.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * spike("ice_spike", state = blockState(Blocks.PACKED_ICE)) {
 *     canPlaceOn { matchingBlockTag(Tags.Block.SNOW) }
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
 *     canReplace { replaceable() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Spike.canReplace(block: BlockPredicatesScope.() -> Unit) {
	canReplace = blockPredicate(block)
}
