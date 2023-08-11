package features.worldgen.configuredfeature.configurations

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class FillLayer(
	var state: BlockState = blockStateStone(),
	var height: Int = 0,
) : FeatureConfig()

fun fillLayer(state: BlockState = blockStateStone(), height: Int = 0) = FillLayer(state, height)
