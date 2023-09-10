package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class AllOf(
	var terms: PredicateAsList? = null,
) : PredicateCondition()

fun Predicate.allOf(terms: Predicate.() -> Unit = {}) {
	predicateConditions += AllOf(
		Predicate()
			.apply(terms)
	)
}
