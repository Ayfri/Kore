package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `fill_layer` feature.
 *
 * Fills one horizontal layer of the chunk with [state]. No vanilla data uses it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fill_layer
 *
 * @property state The block state filling the layer.
 * @property height The layer to fill, `0` to `4064` blocks above the placement position (the world bottom by default).
 */
@Serializable
data class FillLayer(
	var state: BlockState = blockStateStone(),
	var height: Int = 0,
) : FeatureConfig()

/**
 * Creates a `fill_layer` configured feature filling the layer [height] with [state].
 *
 * ```kotlin
 * fillLayer("bedrock_floor", state = blockState(Blocks.BEDROCK), height = 0)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#fill_layer
 */
fun ConfiguredFeatures.fillLayer(fileName: String, state: BlockState = blockStateStone(), height: Int = 0) =
	dp.configuredFeature(fileName, FillLayer(state, height))
