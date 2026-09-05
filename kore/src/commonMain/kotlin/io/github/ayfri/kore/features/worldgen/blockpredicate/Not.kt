package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

/**
 * Passes when [predicate] does not pass.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#not
 *
 * @property predicate The predicate to invert.
 */
@Serializable
data class Not(
	var predicate: BlockPredicate,
) : BlockPredicate()

/**
 * Creates a `not` block predicate, passing when the predicate built in [block] does not pass.
 *
 * Several predicates built in [block] are wrapped in an [AllOf], so the result passes when at least one of them
 * fails.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { not { matchingBlockTag(Tags.Block.LEAVES) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#not
 */
fun BlockPredicateScope.not(block: BlockPredicatesScope.() -> Unit) =
	Not(blockPredicate(block)).also { addBlockPredicate(it) }
