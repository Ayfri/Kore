package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
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
) : FeatureConfig()

/**
 * Creates a [Spike] feature configuration.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spike
 */
fun spike(
	canPlaceOn: BlockPredicate = True,
	canReplace: BlockPredicate = True,
	state: BlockState = blockStateStone(),
	block: Spike.() -> Unit = {},
) = Spike(canPlaceOn, canReplace, state).apply(block)
