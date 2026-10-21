package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.types.PredicateArgument
import kotlinx.serialization.Serializable

/**
 * Passes when [term] does not pass.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - inverted](https://minecraft.wiki/w/Predicate#inverted)
 */
@Serializable
data class Inverted(
	var term: PredicateCondition,
) : PredicateCondition()

/** Adds an [Inverted] condition negating the single condition declared in [term]. */
fun Predicate.inverted(term: Predicate.() -> Unit) {
	predicateConditions += Inverted(singleCondition("inverted", term))
}

/** Adds an [Inverted] condition negating [term]. */
fun Predicate.inverted(term: PredicateCondition) {
	predicateConditions += Inverted(term)
}

/** Adds an [Inverted] condition negating the predicate [term], written as its bare ID. */
fun Predicate.inverted(term: PredicateArgument) {
	predicateConditions += Inverted(PredicateReference(term))
}
