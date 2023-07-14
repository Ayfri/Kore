package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetCount(
	var count: NumberProvider,
	var add: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setCount(count: NumberProvider, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(count, add).apply(block)
}

fun ItemModifier.setCount(count: Float, add: Boolean? = null, block: SetCount.() -> Unit = {}) {
	modifiers += SetCount(constant(count), add).apply(block)
}
