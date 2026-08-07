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
fun ConfiguredFeatures.blockBlob(
	fileName: String,
	canPlaceOn: BlockPredicate = True,
	state: BlockState = blockStateStone(),
	block: BlockBlob.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BlockBlob(canPlaceOn, state).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
