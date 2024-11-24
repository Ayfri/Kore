package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.ItemStackBuilder
import io.github.ayfri.kore.data.item.builders.itemStack

interface CraftingRecipe {
	var result: ItemStack
}

fun CraftingRecipe.result(block: ItemStackBuilder.() -> Unit) {
	result = itemStack(block = block)
}

fun CraftingRecipe.result(item: ItemArgument, count: Short? = null, init: (Components.() -> Unit)? = null) {
	result = itemStack(item, count, init)
}
