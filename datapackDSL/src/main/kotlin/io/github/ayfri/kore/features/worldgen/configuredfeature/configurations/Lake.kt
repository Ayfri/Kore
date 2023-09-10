package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class Lake(
	var fluid: BlockState = blockStateStone(),
	var barrier: BlockState = blockStateStone(),
) : FeatureConfig()

fun lake(fluid: BlockState = blockStateStone(), barrier: BlockState = blockStateStone()) = Lake(fluid, barrier)
