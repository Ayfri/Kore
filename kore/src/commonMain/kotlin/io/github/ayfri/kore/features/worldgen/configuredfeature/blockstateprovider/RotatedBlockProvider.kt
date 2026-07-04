package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class RotatedBlockProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

fun rotatedBlockProvider(state: BlockState = blockStateStone()) = RotatedBlockProvider(state)
