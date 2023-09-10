package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.providers.NumberProvider
import io.github.ayfri.kore.features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetCount(
	override var conditions: PredicateAsList? = null,
	var count: NumberProvider,
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setCount(count: NumberProvider, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count = count, add = add).apply(block)
}

fun ItemModifier.setCount(count: Float, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count = constant(count), add = add).apply(block)
}
