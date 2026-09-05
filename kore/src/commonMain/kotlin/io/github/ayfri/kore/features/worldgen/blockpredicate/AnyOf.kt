package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

/**
 * Passes when at least one predicate of [predicates] passes, an empty list never passing.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#any_of
 *
 * @property predicates The child predicates, at least one of which has to pass.
 */
@Serializable
data class AnyOf(
	var predicates: List<BlockPredicate> = emptyList(),
) : BlockPredicate()

/**
 * Creates an `any_of` block predicate, passing when at least one predicate built in [block] passes.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate {
 *         anyOf {
 *             matchingBlocks(Blocks.STONE)
 *             matchingBlocks(Blocks.DEEPSLATE)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#any_of
 */
fun BlockPredicateScope.anyOf(block: BlockPredicatesScope.() -> Unit) =
	AnyOf(buildBlockPredicates(block)).also { addBlockPredicate(it) }
