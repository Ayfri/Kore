package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.generated.arguments.FluidOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the fluid at [offset] is one of [fluids], given as a single fluid, a list of fluids or a fluid tag.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_fluids
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 * @property fluids The fluids to match, serialized as a bare string when there is exactly one.
 */
@Serializable
data class MatchingFluids(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
	var fluids: InlinableList<FluidOrTagArgument> = emptyList(),
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `matching_fluids` block predicate, passing when the tested fluid is one of [fluids].
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { matchingFluids(Fluids.WATER) { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_fluids
 */
fun BlockPredicateScope.matchingFluids(vararg fluids: FluidOrTagArgument, init: MatchingFluids.() -> Unit = {}) =
	MatchingFluids(fluids = fluids.toList()).apply(init).also { addBlockPredicate(it) }

/**
 * Creates a `matching_fluids` block predicate, passing when the tested fluid is one of [fluids].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_fluids
 */
fun BlockPredicateScope.matchingFluids(
	fluids: InlinableList<FluidOrTagArgument>,
	init: MatchingFluids.() -> Unit = {},
) = MatchingFluids(fluids = fluids).apply(init).also { addBlockPredicate(it) }
