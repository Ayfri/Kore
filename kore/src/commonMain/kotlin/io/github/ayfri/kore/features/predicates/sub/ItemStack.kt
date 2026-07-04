package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	var count: IntRangeOrIntJson? = null,
	var items: InlinableList<ItemOrTagArgument>? = null,
	var components: Components? = null,
	var predicates: ItemStackSubPredicates? = null,
)

fun itemStack(init: ItemStack.() -> Unit = {}) = ItemStack().apply(init)

fun itemStack(item: ItemArgument, init: ItemStack.() -> Unit = {}) = ItemStack(items = listOf(item)).apply(init)

fun ItemStack.item(vararg items: ItemArgument) {
	this.items = items.toList()
}

fun ItemStack.components(block: Components.() -> Unit) {
	components = Components().apply(block)
}

fun ItemStack.predicates(block: ItemStackSubPredicates.() -> Unit) {
	predicates = ItemStackSubPredicates().apply(block)
}
