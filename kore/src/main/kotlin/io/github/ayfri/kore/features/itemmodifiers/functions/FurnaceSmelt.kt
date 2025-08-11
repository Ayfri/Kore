package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Applies furnace smelting to the item if possible.
 * Mirrors vanilla `minecraft:furnace_smelt`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class FurnaceSmelt(override var conditions: PredicateAsList? = null) : ItemFunction()

/** Add a `furnace_smelt` step. */
fun ItemModifier.furnaceSmelt(block: FurnaceSmelt.() -> Unit = {}) {
	modifiers += FurnaceSmelt().apply(block)
}
