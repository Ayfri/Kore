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
data class RuleBasedBlockStateProvider(
	var fallback: BlockStateProvider? = null,
	var rules: List<RuleBasedBlockStateProviderRule> = emptyList(),
) : BlockStateProvider()

/**
 * A single rule inside a [RuleBasedBlockStateProvider].
 *
 * [ifTrue] is tested at the candidate position; when it passes, [then] provides the block state.
 * Defaults to [True] / [simpleStateProvider] so the rule can be configured via the DSL receiver block.
 */
@Serializable
data class RuleBasedBlockStateProviderRule(
	var ifTrue: BlockPredicate = True,
	var then: BlockStateProvider = simpleStateProvider(),
)

/** Creates a [RuleBasedBlockStateProvider] with an optional [fallback] and a fixed [rules] list. */
fun ruleBasedBlockStateProvider(
	fallback: BlockStateProvider? = null,
	rules: List<RuleBasedBlockStateProviderRule> = emptyList(),
) = RuleBasedBlockStateProvider(fallback, rules)

/**
 * Creates a [RuleBasedBlockStateProvider] configured entirely via [block] on the provider itself.
 *
 * Set [RuleBasedBlockStateProvider.fallback] and append rules with the [RuleBasedBlockStateProvider.rule] extensions inside [block].
 */
fun ruleBasedBlockStateProvider(
	block: RuleBasedBlockStateProvider.() -> Unit,
) = RuleBasedBlockStateProvider().apply(block)

/**
 * Sets [RuleBasedBlockStateProviderRule.ifTrue] by building a predicate from a [MutableList] block.
 *
 * A single entry is used as-is; multiple entries are wrapped in [AllOf].
 */
fun RuleBasedBlockStateProviderRule.ifTrue(block: MutableList<BlockPredicate>.() -> Unit) {
	val predicates = buildList(block)
	ifTrue = if (predicates.size == 1) predicates.first() else AllOf(predicates)
}

/** Sets [RuleBasedBlockStateProviderRule.then] to [provider]. */
fun RuleBasedBlockStateProviderRule.then(provider: BlockStateProvider) {
	then = provider
}

/**
 * Appends a [RuleBasedBlockStateProviderRule] with [ifTrue] and [then] set directly.
 */
fun MutableList<RuleBasedBlockStateProviderRule>.rule(
	ifTrue: BlockPredicate,
	then: BlockStateProvider,
) {
	this += RuleBasedBlockStateProviderRule(ifTrue, then)
}

/**
 * Appends a [RuleBasedBlockStateProviderRule] by building [ifTrue] from a [MutableList] predicate block
 * and setting [then] as the block state provider.
 *
 * A single predicate in [ifTrue] is used as-is; multiple are wrapped in [AllOf].
 */
fun MutableList<RuleBasedBlockStateProviderRule>.rule(
	then: BlockStateProvider,
	ifTrue: MutableList<BlockPredicate>.() -> Unit,
) {
	val predicates = buildList(ifTrue)
	val predicate = if (predicates.size == 1) predicates.first() else AllOf(predicates)
	this += RuleBasedBlockStateProviderRule(predicate, then)
}

/**
 * Appends a [RuleBasedBlockStateProviderRule] configured entirely via [block] on the rule itself.
 *
 * Use [RuleBasedBlockStateProviderRule.ifTrue] and [RuleBasedBlockStateProviderRule.then] inside [block].
 */
fun MutableList<RuleBasedBlockStateProviderRule>.rule(
	block: RuleBasedBlockStateProviderRule.() -> Unit,
) {
	this += RuleBasedBlockStateProviderRule().apply(block)
}

/** Appends a [RuleBasedBlockStateProviderRule] with [ifTrue] and [then] set directly. */
fun RuleBasedBlockStateProvider.rule(
	ifTrue: BlockPredicate,
	then: BlockStateProvider,
) {
	rules += RuleBasedBlockStateProviderRule(ifTrue, then)
}

/**
 * Appends a [RuleBasedBlockStateProviderRule] by building [ifTrue] from a [MutableList] predicate block
 * and setting [then] as the block state provider.
 *
 * A single predicate in [ifTrue] is used as-is; multiple are wrapped in [AllOf].
 */
fun RuleBasedBlockStateProvider.rule(
	then: BlockStateProvider,
	ifTrue: MutableList<BlockPredicate>.() -> Unit,
) {
	val predicates = buildList(ifTrue)
	val predicate = if (predicates.size == 1) predicates.first() else AllOf(predicates)
	rules += RuleBasedBlockStateProviderRule(predicate, then)
}

/**
 * Appends a [RuleBasedBlockStateProviderRule] configured entirely via [block] on the rule itself.
 *
 * Use [RuleBasedBlockStateProviderRule.ifTrue] and [RuleBasedBlockStateProviderRule.then] inside [block].
 */
fun RuleBasedBlockStateProvider.rule(
	block: RuleBasedBlockStateProviderRule.() -> Unit,
) {
	rules += RuleBasedBlockStateProviderRule().apply(block)
}
