package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import kotlinx.serialization.Serializable

/**
 * A [BlockStateProvider] that picks a block state by evaluating a list of predicate–provider rules in order,
 * returning the first match, or [fallback] if none match. When [fallback] is `null` and no rule matches,
 * the consuming feature places nothing.
 *
 * Used by `alter_ground` tree decorators and the `tree` feature's `below_trunk_provider`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 */
@Serializable
data class RuleBasedBlockStateProvider(
	var fallback: BlockStateProvider? = null,
	var rules: List<RuleBasedBlockStateProviderRule> = emptyList(),
) : BlockStateProvider()

/**
 * A single rule inside a [RuleBasedBlockStateProvider].
 *
 * [ifTrue] is tested at the candidate position; when it passes, [then] provides the block state.
 */
@Serializable
data class RuleBasedBlockStateProviderRule(
	var ifTrue: BlockPredicate,
	var then: BlockStateProvider,
)

/** Creates a [RuleBasedBlockStateProvider] with an optional [fallback] and a fixed [rules] list. */
fun ruleBasedBlockStateProvider(
	fallback: BlockStateProvider? = null,
	rules: List<RuleBasedBlockStateProviderRule> = emptyList(),
) = RuleBasedBlockStateProvider(fallback, rules)

/** Creates a [RuleBasedBlockStateProvider] building the rule list with [block]. */
fun ruleBasedBlockStateProvider(
	fallback: BlockStateProvider? = null,
	block: MutableList<RuleBasedBlockStateProviderRule>.() -> Unit,
) = RuleBasedBlockStateProvider(fallback, buildList(block))

/** Adds a rule to a [RuleBasedBlockStateProvider] rule list: when [ifTrue] passes, use [then]. */
fun MutableList<RuleBasedBlockStateProviderRule>.rule(ifTrue: BlockPredicate, then: BlockStateProvider) {
	this += RuleBasedBlockStateProviderRule(ifTrue, then)
}
