package features.worldgen.configuredfeature.configurations

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class ForestRock(
	var state: BlockState = blockStateStone(),
) : FeatureConfig()

fun forestRock(state: BlockState = blockStateStone()) = ForestRock(state)
