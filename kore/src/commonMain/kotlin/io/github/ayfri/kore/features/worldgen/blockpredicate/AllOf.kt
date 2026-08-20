package io.github.ayfri.kore.features.worldgen.blockpredicate

import kotlinx.serialization.Serializable

/**
 * Passes when every predicate of [predicates] passes, an empty list always passing.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#all_of
 *
 * @property predicates The child predicates, all of which have to pass.
 */
@Serializable
data class AllOf(
	var predicates: List<BlockPredicate> = emptyList(),
) : BlockPredicate()

/**
 * Creates an `all_of` block predicate, passing when every predicate built in [block] passes.
 *
 * A field taking a block predicate already wraps several predicates in an `all_of`, so this builder is only needed to
 * nest one inside another composite.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate {
 *         anyOf {
 *             allOf {
 *                 solid { offset(0, -1, 0) }
 *                 not { matchingFluids(Fluids.WATER) }
 *             }
 *             matchingBlockTag(Tags.Block.DIRT)
 *         }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#all_of
 */
fun BlockPredicateScope.allOf(block: BlockPredicatesScope.() -> Unit) =
	AllOf(buildBlockPredicates(block)).also { addBlockPredicate(it) }
