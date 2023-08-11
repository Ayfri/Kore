package features.predicates.conditions

import features.predicates.Predicate
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	var terms: PredicateAsList? = null,
) : PredicateCondition()

fun Predicate.allOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AllOf(Predicate().apply(terms))
}
