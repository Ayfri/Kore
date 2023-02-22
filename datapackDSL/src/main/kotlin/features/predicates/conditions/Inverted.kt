package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class Inverted(
	var term: PredicateCondition,
) : PredicateCondition

fun Predicate.inverted(term: Predicate.() -> Unit) {
	predicateConditions += Inverted(Predicate().apply(term).predicateConditions.first())
}
