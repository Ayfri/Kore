package features.itemmodifiers.functions

import arguments.Argument
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	val name: String
) : ItemFunctionSurrogate

fun ItemModifierEntry.reference(name: String) {
	function = Reference(name)
}

fun ItemModifierEntry.reference(itemModifier: Argument.ItemModifier) {
	function = Reference(itemModifier.asString())
}
