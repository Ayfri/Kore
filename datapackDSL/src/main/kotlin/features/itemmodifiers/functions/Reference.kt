package features.itemmodifiers.functions

import arguments.types.resources.ItemModifierArgument
import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
	override var conditions: PredicateAsList? = null,
	val name: String,
) : ItemFunction()

fun ItemModifier.reference(name: String, block: Reference.() -> Unit = {}) {
	modifiers += Reference(name = name).apply(block)
}

fun ItemModifier.reference(itemModifier: ItemModifierArgument, block: Reference.() -> Unit = {}) {
	modifiers += Reference(name = itemModifier.asString()).apply(block)
}
