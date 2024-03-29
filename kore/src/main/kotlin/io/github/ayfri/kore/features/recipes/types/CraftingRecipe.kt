package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.features.recipes.data.CraftingResult

interface CraftingRecipe {
	var result: CraftingResult
}

fun CraftingRecipe.result(block: CraftingResult.() -> Unit) {
	result = CraftingResult(id = item("")).apply(block)
}

fun CraftingRecipe.result(item: ItemArgument, count: Int? = null, components: (Components.() -> Unit)? = null) {
	result = CraftingResult(item, count, components?.let { Components().apply(it) })
}
