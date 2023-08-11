package features.worldgen.configuredfeature.ruletest

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class RandomBlockStateMatch(
	var blockState: BlockState = blockStateStone(),
	var probability: Double = 0.0,
) : RuleTest()

fun randomBlockStateMatch(
	blockState: BlockState = blockStateStone(),
	probability: Double = 0.0,
) = RandomBlockStateMatch(blockState, probability)
