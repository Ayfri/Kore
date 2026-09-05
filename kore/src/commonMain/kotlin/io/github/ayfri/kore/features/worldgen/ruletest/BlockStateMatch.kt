package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Matches a single block state, every property of [blockState] having to match exactly.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property blockState The block state to match.
 */
@Serializable
@SerialName("blockstate_match")
data class BlockStateMatch(
	var blockState: BlockState,
) : RuleTest()

/**
 * Creates a `blockstate_match` rule test, matching [blockState] exactly.
 *
 * ```kotlin
 * rule {
 *     inputPredicate = blockStateMatch(blockState(Blocks.CHEST, "facing" to "north"))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.blockStateMatch(blockState: BlockState) = BlockStateMatch(blockState)
