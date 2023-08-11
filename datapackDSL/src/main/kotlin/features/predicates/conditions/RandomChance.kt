package features.predicates.conditions

import features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class RandomChance(
	var chance: Float,
) : PredicateCondition()

fun Predicate.randomChance(chance: Float) {
	predicateConditions += RandomChance(chance)
}
