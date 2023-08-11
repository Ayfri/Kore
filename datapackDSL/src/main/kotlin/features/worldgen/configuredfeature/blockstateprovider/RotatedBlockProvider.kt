package features.worldgen.configuredfeature.blockstateprovider

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class RotatedBlockProvider(
	var state: BlockState = blockStateStone(),
) : BlockStateProvider()

fun rotatedBlockProvider(state: BlockState = blockStateStone()) = RotatedBlockProvider(state)
