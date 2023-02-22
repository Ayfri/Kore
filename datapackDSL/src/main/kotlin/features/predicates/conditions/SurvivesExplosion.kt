package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
object SurvivesExplosion : PredicateCondition

fun Predicate.survivesExplosion() {
	predicateConditions += SurvivesExplosion
}
