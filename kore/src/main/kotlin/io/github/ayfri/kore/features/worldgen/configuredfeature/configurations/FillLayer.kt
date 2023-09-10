package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class FillLayer(
	var state: BlockState = blockStateStone(),
	var height: Int = 0,
) : FeatureConfig()

fun fillLayer(state: BlockState = blockStateStone(), height: Int = 0) = FillLayer(state, height)
