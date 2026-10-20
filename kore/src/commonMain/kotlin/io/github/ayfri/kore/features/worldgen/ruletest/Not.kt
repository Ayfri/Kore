package io.github.ayfri.kore.features.worldgen.ruletest

import kotlinx.serialization.Serializable

/**
 * Matches a block when [rule] doesn't match it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property rule The rule test to invert.
 */
@Serializable
data class Not(
	var rule: RuleTest,
) : RuleTest()

/**
 * Creates a `not` rule test, matching a block when [rule] doesn't match it.
 *
 * ```kotlin
 * target(blockState(Blocks.IRON_ORE)) {
 *     target = not(blockMatch(Blocks.BEDROCK))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.not(rule: RuleTest) = Not(rule)
