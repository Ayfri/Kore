package features.itemmodifiers.functions

import arguments.types.resources.AttributeArgument
import data.item.AttributeModifier
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetAttributes(
	val modifiers: List<AttributeModifier>
) : ItemFunction()

fun ItemModifier.setAttributes(modifiers: MutableList<AttributeModifier>.() -> Unit = {}) =
	SetAttributes(buildList(modifiers)).also { this.modifiers += it }

fun ItemModifier.setAttribute(attribute: AttributeArgument, modifier: AttributeModifier.() -> Unit = {}) =
	SetAttributes(listOf(AttributeModifier(attribute).apply(modifier))).also { modifiers += it }
