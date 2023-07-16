package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.item
import features.recipes.data.CraftingResult

interface CraftingRecipe {
	var result: CraftingResult
}

fun CraftingRecipe.result(block: CraftingResult.() -> Unit) {
	result = CraftingResult(item = item("")).apply(block)
}

fun CraftingRecipe.result(item: ItemArgument, count: Int? = null) {
	result = CraftingResult(item, count)
}
