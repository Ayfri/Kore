package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.EnchantmentArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.formulas.Formula
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class ApplyBonus(
	override var conditions: PredicateAsList? = null,
	var enchantment: EnchantmentArgument,
	var formula: Formula,
) : ItemFunction()

fun ItemModifier.applyBonus(enchantment: EnchantmentArgument, formula: Formula, block: ApplyBonus.() -> Unit = {}) {
	modifiers += ApplyBonus(enchantment = enchantment, formula = formula).apply(block)
}
