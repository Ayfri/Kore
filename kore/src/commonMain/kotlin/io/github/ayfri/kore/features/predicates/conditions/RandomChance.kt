package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

/**
 * Passes with the probability given by [chance], clamped between `0` and `1`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - random_chance](https://minecraft.wiki/w/Predicate#random_chance)
 */
@Serializable
data class RandomChance(
	var chance: NumberProvider,
) : PredicateCondition()

/** Adds a [RandomChance] condition passing with a fixed probability of [chance]. */
fun Predicate.randomChance(chance: Float) {
	predicateConditions += RandomChance(constant(chance))
}

/** Adds a [RandomChance] condition passing with the probability produced by [chance]. */
fun Predicate.randomChance(chance: NumberProvider) {
	predicateConditions += RandomChance(chance)
}
