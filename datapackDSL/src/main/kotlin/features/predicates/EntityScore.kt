package features.predicates

import features.predicates.providers.IntOrNumberProvidersRange
import features.predicates.types.EntityType
import kotlinx.serialization.Serializable

@Serializable
data class EntityScore(
	var entity: EntityType,
	var scores: Map<String, IntOrNumberProvidersRange>,
) : Predicate
