package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class SetComponents(
	override var conditions: PredicateAsList? = null,
	val components: ComponentsPatch,
) : ItemFunction()

fun ItemModifier.setComponents(components: ComponentsPatch.() -> Unit = {}) =
	SetComponents(components = ComponentsPatch().apply(components)).also { modifiers += it }
