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
 * Configuration for the `block_blob` feature.
 *
 * Places a small blob of blocks (e.g. mossy cobblestone) on the ground. The block used and the
 * surface it can appear on are both configurable.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_blob
 */
@Serializable
data class BlockBlob(
	var canPlaceOn: BlockPredicate = True,
	var state: BlockState = blockStateStone(),
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `block_blob` configured feature, placing blobs of [state] on the blocks matching
 * [BlockBlob.canPlaceOn].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * blockBlob("mossy_blob", state = blockState(Blocks.MOSSY_COBBLESTONE)) {
 *     canPlaceOn { matchingBlockTag(Tags.Block.DIRT) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_blob
 */
fun ConfiguredFeatures.blockBlob(
	fileName: String,
	state: BlockState = blockStateStone(),
	block: BlockBlob.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BlockBlob(state = state).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Sets [BlockBlob.canPlaceOn] to the predicate built in [block], the blocks the blob may be placed on.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * blockBlob("mossy_blob", state = blockState(Blocks.MOSSY_COBBLESTONE)) {
 *     canPlaceOn { matchingBlockTag(Tags.Block.DIRT) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun BlockBlob.canPlaceOn(block: BlockPredicatesScope.() -> Unit) {
	canPlaceOn = blockPredicate(block)
}
