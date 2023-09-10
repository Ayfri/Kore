package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.ItemModifierAsList
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class Sequence(
	override var conditions: PredicateAsList? = null,
	val functions: ItemModifierAsList? = null,
) : ItemFunction()

fun ItemModifier.sequence(block: ItemModifier.() -> Unit = {}) {
	modifiers += Sequence(functions = ItemModifier().apply(block))
}
