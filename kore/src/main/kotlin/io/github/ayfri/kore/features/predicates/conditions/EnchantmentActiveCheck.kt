package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class EnchantmentActiveCheck(
	var active: Boolean,
) : PredicateCondition()

fun Predicate.enchantmentActiveCheck(active: Boolean) {
	predicateConditions += EnchantmentActiveCheck(active)
}
