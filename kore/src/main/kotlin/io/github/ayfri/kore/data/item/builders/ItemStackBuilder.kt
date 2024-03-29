package io.github.ayfri.kore.data.item.builders

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack

data class ItemStackBuilder(var id: ItemArgument, var count: Short? = null) {
	val components = Components()

	fun build() = ItemStack(id.asId(), count, components.takeIf { it.components.isNotEmpty() })
}

fun itemStack(item: ItemArgument, count: Short? = null, init: (Components.() -> Unit)? = null) =
	ItemStackBuilder(item, count).apply { init?.let { components.it() } }.build()
