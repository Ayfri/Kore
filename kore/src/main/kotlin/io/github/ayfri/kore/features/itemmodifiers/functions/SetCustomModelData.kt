package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetCustomModelData(
	override var conditions: PredicateAsList? = null,
	var value: NumberProvider,
) : ItemFunction()

fun ItemModifier.setCustomModelData(value: NumberProvider, block: SetCustomModelData.() -> Unit = {}) =
	SetCustomModelData(value = value).apply(block).also { modifiers += it }

fun ItemModifier.setCustomModelData(value: Int, block: SetCustomModelData.() -> Unit = {}) =
	SetCustomModelData(value = constant(value.toFloat())).apply(block).also { modifiers += it }
