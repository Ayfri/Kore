package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
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
) : FeatureConfig()

/**
 * Creates a [BlockBlob] feature configuration.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_blob
 */
fun blockBlob(
	canPlaceOn: BlockPredicate = True,
	state: BlockState = blockStateStone(),
	block: BlockBlob.() -> Unit = {},
) = BlockBlob(canPlaceOn, state).apply(block)
