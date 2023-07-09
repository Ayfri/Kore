package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import features.itemmodifiers.formulas.Formula
import kotlinx.serialization.Serializable

@Serializable
data class ApplyBonus(
	var enchantment: Argument.Enchantment,
	var formula: Formula,
) : ItemFunctionSurrogate

fun ItemModifierEntry.applyBonus(enchantment: Argument.Enchantment, formula: Formula) {
	function = ApplyBonus(enchantment, formula)
}
