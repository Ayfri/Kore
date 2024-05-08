package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.ComponentsRemovables
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetComponents(
	override var conditions: PredicateAsList? = null,
	val components: ComponentsRemovables,
) : ItemFunction()

fun ItemModifier.setComponents(components: ComponentsRemovables.() -> Unit = {}) =
	SetComponents(components = ComponentsRemovables().apply(components)).also { modifiers += it }
