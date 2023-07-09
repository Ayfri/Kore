package features.itemmodifiers.functions

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class SetPotion(
	val potion: Argument.Potion
) : ItemFunctionSurrogate
