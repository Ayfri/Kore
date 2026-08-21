package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

/**
 * Passes with the probability picked from [chances] at the index given by the level of [enchantment] on the tool.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - table_bonus](https://minecraft.wiki/w/Predicate#table_bonus)
 */
@Serializable
data class TableBonus(
	var enchantment: EnchantmentArgument,
	var chances: List<Float> = emptyList(),
) : PredicateCondition()

/** Adds a [TableBonus] condition, one probability per enchantment level starting at level 0. */
fun Predicate.tableBonus(enchantment: EnchantmentArgument, chances: List<Float>) {
	predicateConditions += TableBonus(enchantment, chances)
}

/** Adds a [TableBonus] condition, one probability per enchantment level starting at level 0. */
fun Predicate.tableBonus(enchantment: EnchantmentArgument, vararg chances: Float) {
	predicateConditions += TableBonus(enchantment, chances.toList())
}

/** Adds a [TableBonus] condition with the probabilities declared in [chances]. */
fun Predicate.tableBonus(enchantment: EnchantmentArgument, chances: MutableList<Float>.() -> Unit) {
	predicateConditions += TableBonus(enchantment, buildList(chances))
}
