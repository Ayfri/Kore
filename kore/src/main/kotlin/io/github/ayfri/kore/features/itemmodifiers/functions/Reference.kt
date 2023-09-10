package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.ItemModifierArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
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
