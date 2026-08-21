package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

/**
 * Passes with [unenchantedChance] when the attacker does not have [enchantment], and with [enchantedChance] evaluated
 * at its level when it does.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - random_chance_with_enchanted_bonus](https://minecraft.wiki/w/Predicate#random_chance_with_enchanted_bonus)
 */
@Serializable
data class RandomChanceWithEnchantedBonus(
	var unenchantedChance: Float,
	var enchantedChance: LevelBased,
	var enchantment: EnchantmentArgument,
) : PredicateCondition()

/** Adds a [RandomChanceWithEnchantedBonus] condition. */
fun Predicate.randomChanceWithEnchantedBonus(unenchantedChance: Float, enchantedChance: LevelBased, enchantment: EnchantmentArgument) {
	predicateConditions += RandomChanceWithEnchantedBonus(unenchantedChance, enchantedChance, enchantment)
}

/** Adds a [RandomChanceWithEnchantedBonus] condition with a constant enchanted chance. */
fun Predicate.randomChanceWithEnchantedBonus(unenchantedChance: Float, enchantedChance: Int, enchantment: EnchantmentArgument) {
	predicateConditions += RandomChanceWithEnchantedBonus(unenchantedChance, LevelBased.constantLevelBased(enchantedChance), enchantment)
}
