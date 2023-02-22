package features.predicates.conditions

import features.predicates.Predicate
import features.predicates.providers.IntOrNumberProvidersRange
import features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityScore(
	var entity: EntityType,
	var scores: Map<String, IntOrNumberProvidersRange>,
) : PredicateCondition

fun Predicate.entityScore(entity: EntityType, scores: Map<String, IntOrNumberProvidersRange>) {
	predicateConditions += EntityScore(entity, scores)
}

fun Predicate.entityScore(entity: EntityType, scores: Map<String, IntOrNumberProvidersRange>.() -> Unit) {
	predicateConditions += EntityScore(entity, buildMap(scores))
}
