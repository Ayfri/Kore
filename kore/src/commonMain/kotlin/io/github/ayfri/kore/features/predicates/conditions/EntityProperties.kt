package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.EntityPredicate
import io.github.ayfri.kore.features.predicates.types.EntityTarget
import kotlinx.serialization.Serializable

/**
 * Passes when the entity designated by [entity] matches [predicate].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - entity_properties](https://minecraft.wiki/w/Predicate#entity_properties)
 */
@Serializable
data class EntityProperties(
	var entity: EntityTarget = EntityTarget.THIS,
	var predicate: EntityPredicate = EntityPredicate(),
) : PredicateCondition()

/** Adds an [EntityProperties] condition matching [entity] against [predicate]. */
fun Predicate.entityProperties(entity: EntityTarget = EntityTarget.THIS, predicate: EntityPredicate) {
	predicateConditions += EntityProperties(entity, predicate)
}

/** Adds an [EntityProperties] condition matching [entity] against the predicate built by [predicate]. */
fun Predicate.entityProperties(entity: EntityTarget = EntityTarget.THIS, predicate: EntityPredicate.() -> Unit = {}) {
	predicateConditions += EntityProperties(entity, EntityPredicate().apply(predicate))
}
