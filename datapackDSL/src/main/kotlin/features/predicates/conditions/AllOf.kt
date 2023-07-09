package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	var terms: List<PredicateCondition>? = null,
) : PredicateCondition

fun Predicate.allOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AllOf(Predicate().apply(terms).predicateConditions.toList())
}
