package helpers

import DataPack
import features.predicates.conditions.randomChance
import features.predicates.predicate

fun DataPack.predicateRandomChance(chance: Float, predicate: String = "random_chance") = predicate(predicate) {
	randomChance(chance)
}
