package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class ForestRock(
	var state: BlockState = blockStateStone(),
) : FeatureConfig()

fun forestRock(state: BlockState = blockStateStone()) = ForestRock(state)
