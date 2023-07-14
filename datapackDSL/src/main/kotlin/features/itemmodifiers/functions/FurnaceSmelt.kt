package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
class FurnaceSmelt : ItemFunction()

fun ItemModifier.furnaceSmelt(block: FurnaceSmelt.() -> Unit = {}) {
	modifiers += FurnaceSmelt().apply(block)
}
