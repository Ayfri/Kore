package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data object FurnaceSmelt : ItemFunctionSurrogate

fun ItemModifierEntry.furnaceSmelt() {
	function = FurnaceSmelt
}
