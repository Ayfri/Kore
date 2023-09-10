package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class AnyOf(
	var terms: PredicateAsList? = null,
) : PredicateCondition()

fun Predicate.anyOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AnyOf(Predicate().apply(terms))
}
