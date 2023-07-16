package features.itemmodifiers.functions

import arguments.types.resources.EnchantmentArgument
import features.itemmodifiers.ItemModifier
import features.itemmodifiers.formulas.Formula
import kotlinx.serialization.Serializable

@Serializable
data class ApplyBonus(
	var enchantment: EnchantmentArgument,
	var formula: Formula,
) : ItemFunction()

fun ItemModifier.applyBonus(enchantment: EnchantmentArgument, formula: Formula, block: ApplyBonus.() -> Unit = {}) {
	modifiers += ApplyBonus(enchantment, formula).apply(block)
}
