package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.features.worldgen.blockpredicate.AllOf
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import kotlinx.serialization.Serializable

/**
 * A [BlockStateProvider] that picks a block state by evaluating a list of predicate–provider rules in order,
 * returning the first match, or [fallback] if none match. When [fallback] is `null` and no rule matches,
 * the consuming feature places nothing.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 */
@Serializable
data class RuleBasedStateProvider(
	var fallback: BlockStateProvider? = null,
	var rules: List<RuleBasedStateProviderRule> = emptyList(),
) : BlockStateProvider()

/**
 * A single rule inside a [RuleBasedStateProvider].
 *
 * [ifTrue] is tested at the candidate position; when it passes, [then] provides the block state.
 * Defaults to [True] / [simpleStateProvider] so the rule can be configured via the DSL receiver block.
 */
@Serializable
data class RuleBasedStateProviderRule(
	var ifTrue: BlockPredicate = True,
	var then: BlockStateProvider = simpleStateProvider(),
)

/** Creates a [RuleBasedStateProvider] with an optional [fallback] and a fixed [rules] list. */
fun ruleBasedStateProvider(
	fallback: BlockStateProvider? = null,
	rules: List<RuleBasedStateProviderRule> = emptyList(),
) = RuleBasedStateProvider(fallback, rules)

/**
 * Creates a [RuleBasedStateProvider] configured entirely via [block] on the provider itself.
 *
 * Set [RuleBasedStateProvider.fallback] and append rules with the [RuleBasedStateProvider.rule] extensions inside [block].
 */
fun ruleBasedStateProvider(
	block: RuleBasedStateProvider.() -> Unit,
) = RuleBasedStateProvider().apply(block)

/**
 * Sets [RuleBasedStateProviderRule.ifTrue] by building a predicate from a [MutableList] block.
 *
 * A single entry is used as-is; multiple entries are wrapped in [AllOf].
 */
fun RuleBasedStateProviderRule.ifTrue(block: MutableList<BlockPredicate>.() -> Unit) {
	val predicates = buildList(block)
	ifTrue = if (predicates.size == 1) predicates.first() else AllOf(predicates)
}

/** Sets [RuleBasedStateProviderRule.then] to [provider]. */
fun RuleBasedStateProviderRule.then(provider: BlockStateProvider) {
	then = provider
}

/**
 * Appends a [RuleBasedStateProviderRule] with [ifTrue] and [then] set directly.
 */
fun MutableList<RuleBasedStateProviderRule>.rule(
	ifTrue: BlockPredicate,
	then: BlockStateProvider,
) {
	this += RuleBasedStateProviderRule(ifTrue, then)
}

/**
 * Appends a [RuleBasedStateProviderRule] by building [ifTrue] from a [MutableList] predicate block
 * and setting [then] as the block state provider.
 *
 * A single predicate in [ifTrue] is used as-is; multiple are wrapped in [AllOf].
 */
fun MutableList<RuleBasedStateProviderRule>.rule(
	then: BlockStateProvider,
	ifTrue: MutableList<BlockPredicate>.() -> Unit,
) {
	val predicates = buildList(ifTrue)
	val predicate = if (predicates.size == 1) predicates.first() else AllOf(predicates)
	this += RuleBasedStateProviderRule(predicate, then)
}

/**
 * Appends a [RuleBasedStateProviderRule] configured entirely via [block] on the rule itself.
 *
 * Use [RuleBasedStateProviderRule.ifTrue] and [RuleBasedStateProviderRule.then] inside [block].
 */
fun MutableList<RuleBasedStateProviderRule>.rule(
	block: RuleBasedStateProviderRule.() -> Unit,
) {
	this += RuleBasedStateProviderRule().apply(block)
}

/** Appends a [RuleBasedStateProviderRule] with [ifTrue] and [then] set directly. */
fun RuleBasedStateProvider.rule(
	ifTrue: BlockPredicate,
	then: BlockStateProvider,
) {
	rules += RuleBasedStateProviderRule(ifTrue, then)
}

/**
 * Appends a [RuleBasedStateProviderRule] by building [ifTrue] from a [MutableList] predicate block
 * and setting [then] as the block state provider.
 *
 * A single predicate in [ifTrue] is used as-is; multiple are wrapped in [AllOf].
 */
fun RuleBasedStateProvider.rule(
	then: BlockStateProvider,
	ifTrue: MutableList<BlockPredicate>.() -> Unit,
) {
	val predicates = buildList(ifTrue)
	val predicate = if (predicates.size == 1) predicates.first() else AllOf(predicates)
	rules += RuleBasedStateProviderRule(predicate, then)
}

/**
 * Appends a [RuleBasedStateProviderRule] configured entirely via [block] on the rule itself.
 *
 * Use [RuleBasedStateProviderRule.ifTrue] and [RuleBasedStateProviderRule.then] inside [block].
 */
fun RuleBasedStateProvider.rule(
	block: RuleBasedStateProviderRule.() -> Unit,
) {
	rules += RuleBasedStateProviderRule().apply(block)
}
