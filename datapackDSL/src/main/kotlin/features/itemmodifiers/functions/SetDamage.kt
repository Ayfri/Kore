package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import features.predicates.providers.NumberProvider
import features.predicates.providers.constant
import kotlinx.serialization.Serializable

@Serializable
data class SetDamage(
	var damage: NumberProvider,
	var add: Boolean? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setDamage(damage: NumberProvider, add: Boolean? = null) {
	function = SetDamage(damage, add)
}

fun ItemModifierEntry.setDamage(damage: Float, add: Boolean? = null) {
	function = SetDamage(constant(damage), add)
}
