package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import kotlinx.serialization.Serializable

/**
 * Matches a single block, whatever its block state properties are.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property block The block to match.
 */
@Serializable
data class BlockMatch(
	var block: BlockArgument,
) : RuleTest()

/**
 * Creates a `block_match` rule test, matching [block] whatever its block state properties are.
 *
 * ```kotlin
 * rule {
 *     inputPredicate = blockMatch(Blocks.STONE_BRICKS)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.blockMatch(block: BlockArgument) = BlockMatch(block)
