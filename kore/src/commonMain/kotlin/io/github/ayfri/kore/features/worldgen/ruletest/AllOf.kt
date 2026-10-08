package io.github.ayfri.kore.features.worldgen.ruletest

import kotlinx.serialization.Serializable

/**
 * Matches a block when every rule test of [rules] matches it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property rules The rule tests to match.
 */
@Serializable
data class AllOf(
	var rules: List<RuleTest>,
) : RuleTest()

/**
 * Creates an `all_of` rule test, matching a block when every rule test of [rules] matches it.
 *
 * ```kotlin
 * target(blockState(Blocks.IRON_ORE)) {
 *     target = allOf(tagMatch(Tags.Block.STONE_ORE_REPLACEABLES), heightMatch(minInclusive = 0, maxInclusive = 64))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.allOf(vararg rules: RuleTest) = AllOf(rules.toList())
