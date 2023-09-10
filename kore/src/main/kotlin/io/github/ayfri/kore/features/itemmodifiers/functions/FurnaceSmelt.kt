package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

@Serializable
data class FurnaceSmelt(override var conditions: PredicateAsList? = null) : ItemFunction()

fun ItemModifier.furnaceSmelt(block: FurnaceSmelt.() -> Unit = {}) {
	modifiers += FurnaceSmelt().apply(block)
}
