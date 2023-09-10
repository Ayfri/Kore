package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityScore(
	var entity: EntityType,
	var scores: Map<String, IntOrNumberProvidersRange>,
) : PredicateCondition()

fun Predicate.entityScore(entity: EntityType, scores: Map<String, IntOrNumberProvidersRange>) {
	predicateConditions += EntityScore(entity, scores)
}

fun Predicate.entityScore(entity: EntityType, scores: MutableMap<String, IntOrNumberProvidersRange>.() -> Unit) {
	predicateConditions += EntityScore(entity, buildMap(scores))
}
