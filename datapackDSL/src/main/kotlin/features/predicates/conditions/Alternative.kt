package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class Alternative(
	var terms: List<PredicateCondition>? = null,
) : PredicateCondition

fun Predicate.alternative(terms: Predicate.() -> Unit = {}) {
	predicateConditions += Alternative(Predicate().apply(terms).predicateConditions.toList())
}
