package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import kotlinx.serialization.Serializable

/**
 * A [BlockStateProvider] picking a block state by evaluating [rules] in order and returning the first match, or
 * [fallback] when none matches.
 *
 * When [fallback] is `null` and no rule matches, the consuming feature places nothing.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 *
 * @property fallback The provider used when no rule matches, placing nothing when `null`.
 * @property rules The rules evaluated in order, the first matching one winning.
 */
@Serializable
data class RuleBasedStateProvider(
	var fallback: BlockStateProvider? = null,
	var rules: List<RuleBasedStateProviderRule> = emptyList(),
) : BlockStateProvider()

/**
 * A single rule of a [RuleBasedStateProvider]: when [ifTrue] passes at the candidate position, [then] provides the
 * block state.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 *
 * @property ifTrue The predicate tested at the candidate position.
 * @property then The provider used when [ifTrue] passes.
 */
@Serializable
data class RuleBasedStateProviderRule(
	var ifTrue: BlockPredicate = True,
	var then: BlockStateProvider = simpleStateProvider(),
) : BlockPredicateScope

/**
 * Creates a `rule_based` block state provider, evaluating its rules in order and falling back to
 * [RuleBasedStateProvider.fallback] when none matches.
 *
 * ```kotlin
 * ruleBasedStateProvider {
 *     fallback = simpleStateProvider(Blocks.DIRT)
 *     rule(simpleStateProvider(Blocks.GRASS_BLOCK)) {
 *         hasSturdyFace(Direction.DOWN)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 */
fun ruleBasedStateProvider(block: RuleBasedStateProvider.() -> Unit = {}) = RuleBasedStateProvider().apply(block)

/**
 * Appends a rule using [then] when the predicate built in [ifTrue] passes.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * ruleBasedStateProvider {
 *     rule(simpleStateProvider(Blocks.GRASS_BLOCK)) {
 *         hasSturdyFace(Direction.DOWN)
 *         not { matchingFluids(Fluids.WATER) }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 */
fun RuleBasedStateProvider.rule(then: BlockStateProvider, ifTrue: BlockPredicatesScope.() -> Unit) {
	rules += RuleBasedStateProviderRule(blockPredicate(ifTrue), then)
}

/**
 * Appends a rule configured entirely through [block], with [RuleBasedStateProviderRule.ifTrue] and
 * [RuleBasedStateProviderRule.then].
 *
 * ```kotlin
 * ruleBasedStateProvider {
 *     rule {
 *         ifTrue { solid() }
 *         then = simpleStateProvider(Blocks.DIRT)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 */
fun RuleBasedStateProvider.rule(block: RuleBasedStateProviderRule.() -> Unit) {
	rules += RuleBasedStateProviderRule().apply(block)
}

/**
 * Sets [RuleBasedStateProviderRule.ifTrue] to the predicate built in [block], the condition the position has to pass.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * rule {
 *     ifTrue { solid() }
 *     then = simpleStateProvider(Blocks.DIRT)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun RuleBasedStateProviderRule.ifTrue(block: BlockPredicatesScope.() -> Unit) {
	ifTrue = blockPredicate(block)
}
