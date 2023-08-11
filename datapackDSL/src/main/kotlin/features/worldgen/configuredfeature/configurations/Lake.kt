package features.worldgen.configuredfeature.configurations

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class Lake(
	var fluid: BlockState = blockStateStone(),
	var barrier: BlockState = blockStateStone(),
) : FeatureConfig()

fun lake(fluid: BlockState = blockStateStone(), barrier: BlockState = blockStateStone()) = Lake(fluid, barrier)
