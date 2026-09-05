package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.data.block.BlockState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Matches a single block state, every property having to match exactly, with a [probability] chance.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property blockState The block state to match.
 * @property probability The chance to match it, clamped between `0.0` and `1.0`.
 */
@Serializable
@SerialName("random_blockstate_match")
data class RandomBlockStateMatch(
	var blockState: BlockState,
	var probability: Double,
) : RuleTest()

/**
 * Creates a `random_blockstate_match` rule test, matching [blockState] with a [probability] chance.
 *
 * ```kotlin
 * rule {
 *     inputPredicate = randomBlockStateMatch(blockState(Blocks.STONE), 0.5)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.randomBlockStateMatch(
	blockState: BlockState,
	probability: Double,
	init: RandomBlockStateMatch.() -> Unit = {},
) = RandomBlockStateMatch(blockState, probability).apply(init)
