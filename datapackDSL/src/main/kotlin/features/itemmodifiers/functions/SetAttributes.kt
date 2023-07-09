package features.itemmodifiers.functions

import arguments.Argument
import data.item.AttributeModifier
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetAttributes(
	val modifiers: List<AttributeModifier>
) : ItemFunctionSurrogate

fun ItemModifierEntry.setAttributes(modifiers: MutableList<AttributeModifier>.() -> Unit = {}) {
	function = SetAttributes(buildList(modifiers))
}

fun ItemModifierEntry.setAttribute(attribute: Argument.Attribute, modifier: AttributeModifier.() -> Unit = {}) {
	function = SetAttributes(listOf(AttributeModifier(attribute).apply(modifier)))
}
