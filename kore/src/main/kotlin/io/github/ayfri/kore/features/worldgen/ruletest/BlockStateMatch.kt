package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("blockstate_match")
data class BlockStateMatch(
	var blockState: BlockState = blockStateStone(),
) : RuleTest()

fun blockStateMatch(blockState: BlockState = blockStateStone()) = BlockStateMatch(blockState)
