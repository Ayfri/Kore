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
 * Configuration for the `block_blob` feature.
 *
 * Places a rounded boulder of [state] on the ground. Vanilla builds the mossy cobblestone boulders of the old growth
 * taigas with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_blob
 *
 * @property canPlaceOn The blocks the boulder can be placed on.
 * @property state The block state the boulder is made of.
 */
@Serializable
data class BlockBlob(
	var canPlaceOn: BlockPredicate = True,
	var state: BlockState = blockStateStone(),
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `block_blob` configured feature, placing boulders of [state] on the blocks matching
 * [BlockBlob.canPlaceOn].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * blockBlob("mossy_boulder", state = blockState(Blocks.MOSSY_COBBLESTONE)) {
 *     canPlaceOn { matchingBlockTag(Tags.Block.FOREST_ROCK_CAN_PLACE_ON) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_blob
 */
fun ConfiguredFeatures.blockBlob(
	fileName: String,
	state: BlockState = blockStateStone(),
	block: BlockBlob.() -> Unit = {},
) = dp.configuredFeature(fileName, BlockBlob(state = state).apply(block))

/**
 * Sets [BlockBlob.canPlaceOn] to the predicate built in [block], the blocks the boulder may be placed on.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * blockBlob("mossy_boulder", state = blockState(Blocks.MOSSY_COBBLESTONE)) {
 *     canPlaceOn { matchingBlockTag(Tags.Block.FOREST_ROCK_CAN_PLACE_ON) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun BlockBlob.canPlaceOn(block: BlockPredicatesScope.() -> Unit) {
	canPlaceOn = blockPredicate(block)
}
