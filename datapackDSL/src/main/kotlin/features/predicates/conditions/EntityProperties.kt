package features.predicates.conditions

import features.advancements.types.Entity
import features.advancements.types.entity
import features.predicates.Predicate
import features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityProperties(
	var entity: EntityType? = null,
	var predicate: Entity? = null,
) : PredicateCondition

fun Predicate.entityProperties(entity: EntityType? = null, predicate: Entity? = null) {
	predicateConditions += EntityProperties(entity, predicate)
}


fun Predicate.entityProperties(entity: EntityType? = null, predicate: Entity.() -> Unit) {
	predicateConditions += EntityProperties(entity, entity(predicate))
}
