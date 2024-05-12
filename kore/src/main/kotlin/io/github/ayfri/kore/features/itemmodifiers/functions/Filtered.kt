package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.features.predicates.sub.ItemStack
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Filtered(
	override var conditions: PredicateAsList? = null,
	var itemFilter: ItemStack = ItemStack(),
	var modifier: InlinableList<ItemFunction> = emptyList(),
) : ItemFunction()

fun ItemModifier.filtered(itemFilter: ItemStack = ItemStack(), block: Filtered.() -> Unit = {}) =
	Filtered(itemFilter = itemFilter).apply(block).also { modifiers += it }

fun Filtered.itemFilter(block: ItemStack.() -> Unit) {
	itemFilter = ItemStack().apply(block)
}

fun Filtered.itemFilter(vararg items: ItemOrTagArgument, block: ItemStack.() -> Unit = {}) {
	itemFilter = ItemStack(items = items.toList()).apply(block)
}

fun Filtered.modifiers(block: ItemModifier.() -> Unit) {
	modifier += ItemModifier().apply(block).modifiers
}
