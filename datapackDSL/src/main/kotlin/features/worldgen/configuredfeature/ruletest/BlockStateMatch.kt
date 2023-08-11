package features.worldgen.configuredfeature.ruletest

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.Serializable

@Serializable
data class BlockStateMatch(
	var blockState: BlockState = blockStateStone(),
) : RuleTest()

fun blockStateMatch(blockState: BlockState = blockStateStone()) = BlockStateMatch(blockState)
