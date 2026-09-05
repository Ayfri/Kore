package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.IntOrNumberProvidersRange
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import kotlinx.serialization.Serializable

/**
 * Passes when every scoreboard objective of [scores] holds a value within its range for the entity designated by [entity].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - entity_scores](https://minecraft.wiki/w/Predicate#entity_scores)
 */
@Serializable
data class EntityScores(
	var entity: EntityTarget = EntityTarget.THIS,
	var scores: Map<String, IntOrNumberProvidersRange> = emptyMap(),
) : PredicateCondition()

/** Adds an [EntityScores] condition matching [scores] on [entity]. */
fun Predicate.entityScores(entity: EntityTarget = EntityTarget.THIS, scores: Map<String, IntOrNumberProvidersRange>) {
	predicateConditions += EntityScores(entity, scores)
}

/** Adds an [EntityScores] condition matching the scores declared in [scores] on [entity]. */
fun Predicate.entityScores(entity: EntityTarget = EntityTarget.THIS, scores: MutableMap<String, IntOrNumberProvidersRange>.() -> Unit) {
	predicateConditions += EntityScores(entity, buildMap(scores))
}
