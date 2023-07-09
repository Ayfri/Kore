package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class AnyOf(
	var terms: List<PredicateCondition>? = null,
) : PredicateCondition

fun Predicate.anyOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AnyOf(Predicate().apply(terms).predicateConditions.toList())
}
