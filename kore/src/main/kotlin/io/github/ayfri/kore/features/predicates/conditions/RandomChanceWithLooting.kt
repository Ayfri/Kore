package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class RandomChanceWithLooting(
	var chance: Float,
	var lootingMultiplier: Float,
) : PredicateCondition()

fun Predicate.randomChanceWithLooting(chance: Float, lootingMultiplier: Float) {
	predicateConditions += RandomChanceWithLooting(chance, lootingMultiplier)
}
