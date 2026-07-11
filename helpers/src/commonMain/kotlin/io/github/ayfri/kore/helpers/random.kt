package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.predicate

/**
 * Creates a random chance predicate with the given chance.
 * Usually used in [Function.execute] command to execute a command with a random chance.
 *
 * Example:
 * ```kotlin
 * execute {
 *     ifCondition(predicateRandomChance(0.5))
 *     run {
 *         say("Hello")
 *     }
 * }
 * ```
 *
 * Will create a predicate and reference the predicate in the execute command with a `random_chance` name.
 */
fun DataPack.predicateRandomChance(chance: Float, predicate: String = "random_chance") = predicate(predicate) {
	randomChance(chance)
}
