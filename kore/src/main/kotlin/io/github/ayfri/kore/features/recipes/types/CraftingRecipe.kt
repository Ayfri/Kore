package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.recipes.data.CraftingResult

interface CraftingRecipe {
	var result: CraftingResult
}

fun CraftingRecipe.result(block: CraftingResult.() -> Unit) {
	result = CraftingResult("").apply(block)
}

fun CraftingRecipe.result(item: ItemArgument, count: Short? = null, init: (ComponentsPatch.() -> Unit)? = null) {
	result = CraftingResult(item.asId(), count,  init?.let { ComponentsPatch().apply(it) })
}
