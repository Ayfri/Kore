package features.predicates

import kotlinx.serialization.Serializable

@Serializable
data class KilledByPlayer(
	var inverse: Boolean? = null,
) : Predicate
