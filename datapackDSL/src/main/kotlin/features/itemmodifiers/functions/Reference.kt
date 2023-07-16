package features.itemmodifiers.functions

import arguments.types.resources.ItemModifierArgument
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	val name: String
) : ItemFunction()

fun ItemModifier.reference(name: String, block: Reference.() -> Unit = {}) {
	modifiers += Reference(name).apply(block)
}

fun ItemModifier.reference(itemModifier: ItemModifierArgument, block: Reference.() -> Unit = {}) {
	modifiers += Reference(itemModifier.asString()).apply(block)
}
