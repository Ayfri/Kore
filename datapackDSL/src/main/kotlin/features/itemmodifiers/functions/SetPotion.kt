package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetPotion(
	val potion: Argument.Potion
) : ItemFunctionSurrogate

fun ItemModifierEntry.setPotion(potion: Argument.Potion) {
	function = SetPotion(potion)
}
