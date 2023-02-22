package features.predicates

import features.advancements.types.DamageSource
import kotlinx.serialization.Serializable

@Serializable
data class DamageSourceProperties(
	var predicate: DamageSource,
) : Predicate
