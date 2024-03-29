package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetComponents(
	override var conditions: PredicateAsList? = null,
	val components: Components,
) : ItemFunction()

fun ItemModifier.setComponents(components: Components.() -> Unit = {}) =
	SetComponents(components = Components().apply(components)).also { modifiers += it }
