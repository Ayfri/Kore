package io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import kotlinx.serialization.Serializable

/**
 * Picks a block state by evaluating [rules] in order, the first rule that yields a result winning, and falls back to
 * [fallback] when none does.
 *
 * When no rule nor the [fallback] yields one, the provider yields none.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 *
 * @property fallback The provider used when no rule yields a result, yielding none when `null`.
 * @property rules The rules evaluated in order, the first one that yields a result winning.
 */
@Serializable
data class RuleBasedBlockStateProvider(
	var fallback: BlockStateProvider? = null,
	var rules: List<RuleBasedBlockStateProviderRule> = emptyList(),
) : BlockStateProvider(), BlockStateProviderScope

/**
 * A single rule of a [RuleBasedBlockStateProvider]: when [ifTrue] passes at the candidate position, [then] provides the
 * block state.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_state_provider#rule_based
 *
 * @property ifTrue The predicate tested at the candidate position.
 * @property then The provider used when [ifTrue] passes.
 */
@Serializable
data class RuleBasedBlockStateProviderRule(
	var ifTrue: BlockPredicate = True,
	var then: BlockStateProvider = SimpleBlockStateProvider(),
) : BlockPredicateScope, BlockStateProviderScope

/**
 * Creates a `rule_based` block state provider, evaluating its rules in order and falling back to
 * [RuleBasedBlockStateProvider.fallback] when none yields a result.
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
fun BlockStateProviderScope.ruleBasedStateProvider(block: RuleBasedBlockStateProvider.() -> Unit = {}) =
	RuleBasedBlockStateProvider().apply(block)

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
fun RuleBasedBlockStateProvider.rule(then: BlockStateProvider, ifTrue: BlockPredicatesScope.() -> Unit) {
	rules += RuleBasedBlockStateProviderRule(blockPredicate(ifTrue), then)
}

/**
 * Appends a rule configured entirely through [block], with [RuleBasedBlockStateProviderRule.ifTrue] and
 * [RuleBasedBlockStateProviderRule.then].
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
fun RuleBasedBlockStateProvider.rule(block: RuleBasedBlockStateProviderRule.() -> Unit) {
	rules += RuleBasedBlockStateProviderRule().apply(block)
}

/**
 * Sets [RuleBasedBlockStateProviderRule.ifTrue] to the predicate built in [block], the condition the position has to pass.
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
fun RuleBasedBlockStateProviderRule.ifTrue(block: BlockPredicatesScope.() -> Unit) {
	ifTrue = blockPredicate(block)
}
