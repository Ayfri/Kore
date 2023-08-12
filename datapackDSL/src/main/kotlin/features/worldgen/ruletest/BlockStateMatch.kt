package features.worldgen.ruletest

import data.block.BlockState
import data.block.blockStateStone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("blockstate_match")
data class BlockStateMatch(
	var blockState: BlockState = blockStateStone(),
) : RuleTest()

fun blockStateMatch(blockState: BlockState = blockStateStone()) = BlockStateMatch(blockState)
