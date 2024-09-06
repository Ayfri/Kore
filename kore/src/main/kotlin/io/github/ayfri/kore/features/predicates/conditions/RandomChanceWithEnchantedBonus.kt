package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.enchantments.values.LevelBased
import io.github.ayfri.kore.features.enchantments.values.constantLevelBased
import io.github.ayfri.kore.features.predicates.Predicate
import kotlinx.serialization.Serializable

@Serializable
data class RandomChanceWithEnchantedBonus(
	var chance: LevelBased,
	var enchantment: EnchantmentArgument,
) : PredicateCondition()

fun Predicate.randomChanceWithEnchantedBonus(chance: LevelBased, enchantment: EnchantmentArgument) {
	predicateConditions += RandomChanceWithEnchantedBonus(chance, enchantment)
}

fun Predicate.randomChanceWithEnchantedBonus(chance: Int, enchantment: EnchantmentArgument) {
	predicateConditions += RandomChanceWithEnchantedBonus(constantLevelBased(chance), enchantment)
}
