package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityScores(
	var entity: EntityType,
	var scores: Map<String, IntOrNumberProvidersRange>,
) : PredicateCondition()

fun Predicate.entityScores(entity: EntityType, scores: Map<String, IntOrNumberProvidersRange>) {
	predicateConditions += EntityScores(entity, scores)
}

fun Predicate.entityScores(entity: EntityType, scores: MutableMap<String, IntOrNumberProvidersRange>.() -> Unit) {
	predicateConditions += EntityScores(entity, buildMap(scores))
}
