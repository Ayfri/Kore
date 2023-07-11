package features.recipes.types

import arguments.Argument
import arguments.item
import features.recipes.data.CraftingResult

interface CraftingRecipe {
	var result: CraftingResult
}

fun CraftingRecipe.result(block: CraftingResult.() -> Unit) {
	result = CraftingResult(item = item("")).apply(block)
}

fun CraftingRecipe.result(item: Argument.Item, count: Int? = null) {
	result = CraftingResult(item, count)
}
