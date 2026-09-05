package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Replaces the current item with the specified item id. Mirrors `minecraft:set_item`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetItem(
	override var conditions: PredicateAsList? = null,
	var item: ItemArgument,
) : ItemFunction()

/** Add a `set_item` step. */
fun ItemModifier.setItem(item: ItemArgument, block: SetItem.() -> Unit = {}) =
	SetItem(item = item).apply(block).also { modifiers += it }
