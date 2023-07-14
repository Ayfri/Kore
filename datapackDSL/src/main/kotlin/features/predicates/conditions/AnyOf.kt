package features.predicates.conditions

import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class AnyOf(
	var terms: PredicateAsList? = null,
) : PredicateCondition

fun Predicate.anyOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AnyOf(Predicate().apply(terms))
}
