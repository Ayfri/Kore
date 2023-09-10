package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.advancements.types.Entity
import io.github.ayfri.kore.features.advancements.types.entity
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityProperties(
	var entity: EntityType? = null,
	var predicate: Entity? = null,
) : PredicateCondition()

fun Predicate.entityProperties(entity: EntityType? = null, predicate: Entity? = null) {
	predicateConditions += EntityProperties(entity, predicate)
}

fun Predicate.entityProperties(entity: EntityType? = null, predicate: Entity.() -> Unit) {
	predicateConditions += EntityProperties(entity, entity(predicate))
}
