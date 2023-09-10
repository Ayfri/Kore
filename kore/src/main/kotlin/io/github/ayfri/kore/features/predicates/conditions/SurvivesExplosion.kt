package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data object SurvivesExplosion : PredicateCondition()

fun Predicate.survivesExplosion() {
	predicateConditions += SurvivesExplosion
}
