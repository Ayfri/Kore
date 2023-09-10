package io.github.ayfri.kore.helpers

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.features.predicates.conditions.randomChance
import io.github.ayfri.kore.features.predicates.predicate

fun DataPack.predicateRandomChance(chance: Float, predicate: String = "random_chance") = predicate(predicate) {
	randomChance(chance)
}
