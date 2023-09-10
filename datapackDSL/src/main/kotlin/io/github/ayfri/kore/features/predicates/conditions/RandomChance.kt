package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class RandomChance(
	var chance: Float,
) : PredicateCondition()

fun Predicate.randomChance(chance: Float) {
	predicateConditions += RandomChance(chance)
}
