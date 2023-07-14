package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.formulas.Formula
import kotlinx.serialization.Serializable

@Serializable
data class ApplyBonus(
	var enchantment: Argument.Enchantment,
	var formula: Formula,
) : ItemFunction()

fun ItemModifier.applyBonus(enchantment: Argument.Enchantment, formula: Formula, block: ApplyBonus.() -> Unit = {}) {
	modifiers += ApplyBonus(enchantment, formula).apply(block)
}
