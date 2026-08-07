package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
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
) : FeatureConfig()

/**
 * Creates a [Spike] feature configuration.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#spike
 */
fun ConfiguredFeatures.spike(
	fileName: String,
	canPlaceOn: BlockPredicate = True,
	canReplace: BlockPredicate = True,
	state: BlockState = blockStateStone(),
	block: Spike.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Spike(canPlaceOn, canReplace, state).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
