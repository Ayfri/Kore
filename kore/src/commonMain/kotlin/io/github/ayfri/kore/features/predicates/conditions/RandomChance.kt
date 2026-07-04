package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class RandomChance(
	var chance: NumberProvider,
) : PredicateCondition()

fun Predicate.randomChance(chance: Float) {
	predicateConditions += RandomChance(constant(chance))
}

fun Predicate.randomChance(chance: NumberProvider) {
	predicateConditions += RandomChance(chance)
}
