package features.predicates

import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	var name: String,
) : Predicate
