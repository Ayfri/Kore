package features.predicates

import kotlinx.serialization.Serializable

@Serializable
data class RandomChanceWithLooting(
	var chance: Float,
	var lootingMultiplier: Float,
) : Predicate
