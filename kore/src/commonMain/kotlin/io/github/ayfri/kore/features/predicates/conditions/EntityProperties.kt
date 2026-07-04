package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityProperties(
	var entity: EntityType = EntityType.THIS,
	var predicate: Entity? = null,
) : PredicateCondition()

fun Predicate.entityProperties(entity: EntityType = EntityType.THIS, predicate: Entity? = null) {
	predicateConditions += EntityProperties(entity, predicate)
}

fun Predicate.entityProperties(entity: EntityType = EntityType.THIS, predicate: Entity.() -> Unit) {
	predicateConditions += EntityProperties(entity, Entity().apply(predicate))
}
