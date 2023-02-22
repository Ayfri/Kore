package features.predicates

import kotlinx.serialization.Serializable

@Serializable
data class RandomChance(
	var chance: Float,
) : Predicate
