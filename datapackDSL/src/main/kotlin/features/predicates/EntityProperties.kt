package features.predicates

import features.advancements.types.Entity
import features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityProperties(
	var entity: EntityType? = null,
	var predicate: Entity? = null,
) : Predicate
