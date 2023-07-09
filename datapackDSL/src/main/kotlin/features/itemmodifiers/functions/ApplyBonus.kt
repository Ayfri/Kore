package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.formulas.Formula
import kotlinx.serialization.Serializable

@Serializable
data class ApplyBonus(
	var enchantment: Argument.Enchantment,
	var formula: Formula,
) : ItemFunctionSurrogate
