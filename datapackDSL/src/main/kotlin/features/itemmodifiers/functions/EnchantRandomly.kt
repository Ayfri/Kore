package features.itemmodifiers.functions

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class EnchantRandomly(
	val enchantments: List<Argument.Enchantment>,
) : ItemFunctionSurrogate
