package features.predicates

import kotlinx.serialization.Serializable

@Serializable
data class Inverted(
	var term: Predicate,
) : Predicate
