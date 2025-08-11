package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Conditional wrapper that applies nested item functions only if the item matches the provided
 * [itemFilter]. Equivalent to vanilla `minecraft:filtered`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class Filtered(
	override var conditions: PredicateAsList? = null,
	var itemFilter: ItemStack = ItemStack(),
	var modifier: InlinableList<ItemFunction> = emptyList(),
) : ItemFunction()

/** Add a `filtered` step to this modifier. */
fun ItemModifier.filtered(itemFilter: ItemStack = ItemStack(), block: Filtered.() -> Unit = {}) =
	Filtered(itemFilter = itemFilter).apply(block).also { modifiers += it }

/** Set the item filter for this `filtered` step. */
fun Filtered.itemFilter(block: ItemStack.() -> Unit) {
	itemFilter = ItemStack().apply(block)
}

/** Set the item filter for this `filtered` step. */
fun Filtered.itemFilter(vararg items: ItemOrTagArgument, block: ItemStack.() -> Unit = {}) {
	itemFilter = ItemStack(items = items.toList()).apply(block)
}

/** Append nested item functions that will run when the [itemFilter] matches. */
fun Filtered.modifiers(block: ItemModifier.() -> Unit) {
	modifier += ItemModifier().apply(block).modifiers
}
