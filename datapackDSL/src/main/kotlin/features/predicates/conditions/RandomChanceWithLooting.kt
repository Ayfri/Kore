package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class RandomChanceWithLooting(
	var chance: Float,
	var lootingMultiplier: Float,
) : PredicateCondition

fun Predicate.randomChanceWithLooting(chance: Float, lootingMultiplier: Float) {
	predicateConditions += RandomChanceWithLooting(chance, lootingMultiplier)
}
