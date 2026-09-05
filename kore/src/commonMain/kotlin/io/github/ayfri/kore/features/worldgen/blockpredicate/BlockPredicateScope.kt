package io.github.ayfri.kore.features.worldgen.blockpredicate

/**
 * Builder scope for block predicates, the tests on the state of a block shared by the placed features, the configured
 * features and the enchantment effects.
 *
 * Every block predicate builder (e.g. [solid], [matchingBlocks]) is an extension on this interface, so they only
 * resolve inside a block that actually accepts a block predicate.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
interface BlockPredicateScope {
	/**
	 * Collects a predicate built inside this scope.
	 *
	 * Scopes holding a single predicate ignore it and rely on the value returned by the builder, while the collecting
	 * scopes such as [BlockPredicatesScope] append it to their list.
	 */
	fun addBlockPredicate(predicate: BlockPredicate) = Unit
}

/**
 * Builder scope collecting every block predicate built inside it, used by `allOf { }`, `anyOf { }`, `not { }` and by
 * every field taking a block predicate.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 *
 * @property predicates The predicates appended so far.
 */
class BlockPredicatesScope : BlockPredicateScope {
	val predicates = mutableListOf<BlockPredicate>()

	override fun addBlockPredicate(predicate: BlockPredicate) {
		predicates += predicate
	}
}

/**
 * Builds a single block predicate out of [block], inlining a lone predicate and wrapping several of them in an
 * [AllOf], so every field taking a block predicate reads the same way.
 *
 * Wrap the body in `anyOf { }` when the predicates have to be combined with an *or* instead.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     // A single predicate is used as-is.
 *     predicate { solid() }
 * }
 *
 * blockPredicateFilter {
 *     // Several predicates are wrapped in an `all_of`.
 *     predicate {
 *         solid { offset(0, -1, 0) }
 *         not { matchingFluids(Fluids.WATER) }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun blockPredicate(block: BlockPredicatesScope.() -> Unit): BlockPredicate {
	val predicates = BlockPredicatesScope().apply(block).predicates
	return when (predicates.size) {
		0 -> True
		1 -> predicates.single()
		else -> AllOf(predicates)
	}
}

/** Collects the predicates built in [block] into a list. */
internal fun buildBlockPredicates(block: BlockPredicatesScope.() -> Unit) = BlockPredicatesScope().apply(block).predicates
