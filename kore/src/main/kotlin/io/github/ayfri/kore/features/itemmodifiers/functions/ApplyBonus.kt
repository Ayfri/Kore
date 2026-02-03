package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.formulas.Formula
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.Serializable

/**
 * Applies a predefined bonus formula to the item stack count based on an enchantment level.
 * Mirrors vanilla `minecraft:apply_bonus` with formula types such as binomial_with_bonus_count,
 * uniform_bonus_count, and ore_drops.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class ApplyBonus(
	override var conditions: PredicateAsList? = null,
	var enchantment: EnchantmentArgument,
	var formula: Formula,
) : ItemFunction()

/** Add an `apply_bonus` step to this modifier. */
fun ItemModifier.applyBonus(enchantment: EnchantmentArgument, formula: Formula, block: ApplyBonus.() -> Unit = {}) {
	modifiers += ApplyBonus(enchantment = enchantment, formula = formula).apply(block)
}
