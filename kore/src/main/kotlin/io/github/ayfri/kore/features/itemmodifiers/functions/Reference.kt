package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.ItemModifierArgument
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
