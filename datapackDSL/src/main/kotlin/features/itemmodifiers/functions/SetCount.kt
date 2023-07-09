package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetCount(
	var count: NumberProvider,
	var add: Boolean? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setCount(count: NumberProvider, add: Boolean? = null) {
	function = SetCount(count, add)
}

fun ItemModifierEntry.setCount(count: Float, add: Boolean? = null) {
	function = SetCount(constant(count), add)
}
