package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data object ExplosionDecay : ItemFunctionSurrogate

fun ItemModifierEntry.explosionDecay() {
	function = ExplosionDecay
}
