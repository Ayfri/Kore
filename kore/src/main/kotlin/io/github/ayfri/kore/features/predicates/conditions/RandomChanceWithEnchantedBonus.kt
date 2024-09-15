package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class RandomChanceWithEnchantedBonus(
	var unenchantedChance: Float,
	var enchantedChance: LevelBased,
	var enchantment: EnchantmentArgument,
) : PredicateCondition()

fun Predicate.randomChanceWithEnchantedBonus(unenchantedChance: Float, enchantedChance: LevelBased, enchantment: EnchantmentArgument) {
	predicateConditions += RandomChanceWithEnchantedBonus(unenchantedChance, enchantedChance, enchantment)
}

fun Predicate.randomChanceWithEnchantedBonus(unenchantedChance: Float, enchantedChance: Int, enchantment: EnchantmentArgument) {
	predicateConditions += RandomChanceWithEnchantedBonus(unenchantedChance, constantLevelBased(enchantedChance), enchantment)
}
