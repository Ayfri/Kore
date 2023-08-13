package features.itemmodifiers.functions

import arguments.types.resources.EnchantmentArgument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.formulas.Formula
import features.predicates.PredicateAsList
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
