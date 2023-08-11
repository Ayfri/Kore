package features.worldgen.configuredfeature.configurations

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class Iceberg(
	var state: BlockState = blockStateStone(),
) : FeatureConfig()

fun iceberg(state: BlockState = blockStateStone()) = Iceberg(state)
