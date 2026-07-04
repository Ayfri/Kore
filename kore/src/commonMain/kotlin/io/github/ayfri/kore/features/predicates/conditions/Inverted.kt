package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class Inverted(
	var term: PredicateCondition,
) : PredicateCondition()

fun Predicate.inverted(term: Predicate.() -> Unit) {
	predicateConditions += Inverted(Predicate().apply(term).predicateConditions.first())
}
