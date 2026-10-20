package io.github.ayfri.kore.features.worldgen.ruletest

import kotlinx.serialization.Serializable

/**
 * Matches a block when at least one rule test of [rules] matches it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 *
 * @property rules The rule tests to match.
 */
@Serializable
data class AnyOf(
	var rules: List<RuleTest>,
) : RuleTest()

/**
 * Creates an `any_of` rule test, matching a block when at least one rule test of [rules] matches it.
 *
 * ```kotlin
 * target(blockState(Blocks.IRON_ORE)) {
 *     target = anyOf(tagMatch(Tags.Block.STONE_ORE_REPLACEABLES), tagMatch(Tags.Block.DEEPSLATE_ORE_REPLACEABLES))
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
fun RuleTestScope.anyOf(vararg rules: RuleTest) = AnyOf(rules.toList())
