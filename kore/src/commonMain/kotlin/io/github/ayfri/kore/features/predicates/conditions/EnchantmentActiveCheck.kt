package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

/**
 * Passes when the enchantment being evaluated is active, or inactive when [active] is `false`.
 *
 * Requires the enchantment active parameter in the loot context, so it only works inside enchantment conditions.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - enchantment_active_check](https://minecraft.wiki/w/Predicate#enchantment_active_check)
 */
@Serializable
data class EnchantmentActiveCheck(
	var active: Boolean,
) : PredicateCondition()

/** Adds an [EnchantmentActiveCheck] condition. */
fun Predicate.enchantmentActiveCheck(active: Boolean) {
	predicateConditions += EnchantmentActiveCheck(active)
}
