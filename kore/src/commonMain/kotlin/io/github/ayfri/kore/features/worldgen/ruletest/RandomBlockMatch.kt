package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import kotlinx.serialization.Serializable

/**
 * Matches a single block, whatever its block state properties are, with a [probability] chance.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property block The block to match.
 * @property probability The chance to match it, clamped between `0.0` and `1.0`.
 */
@Serializable
data class RandomBlockMatch(
	var block: BlockArgument,
	var probability: Double,
) : RuleTest()

/**
 * Creates a `random_block_match` rule test, matching [block] with a [probability] chance.
 *
 * ```kotlin
 * rule {
 *     inputPredicate = randomBlockMatch(Blocks.COBBLESTONE, 0.1)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.randomBlockMatch(block: BlockArgument, probability: Double, init: RandomBlockMatch.() -> Unit = {}) =
	RandomBlockMatch(block, probability).apply(init)
