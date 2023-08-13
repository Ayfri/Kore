package features.itemmodifiers.functions

import features.itemmodifiers.ItemModifier
import features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class FurnaceSmelt(override var conditions: PredicateAsList? = null) : ItemFunction()

fun ItemModifier.furnaceSmelt(block: FurnaceSmelt.() -> Unit = {}) {
	modifiers += FurnaceSmelt().apply(block)
}
