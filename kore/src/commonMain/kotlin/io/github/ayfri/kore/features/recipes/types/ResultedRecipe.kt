package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.features.recipes.data.TransmuteResult


/** Shared interface for recipes that produce an output item. */
interface ResultedRecipe {
	var result: CraftingResult
}

/** Sets the result from an existing [ItemStack]. */
fun ResultedRecipe.result(itemStack: ItemStack) { result = CraftingResult(itemStack.id, itemStack.count, itemStack.components?.toPatch()) }

/** Sets the result using a [CraftingResult] builder block. */
fun ResultedRecipe.result(block: CraftingResult.() -> Unit) { result = CraftingResult("").apply(block) }

/** Sets the result to [item] with an optional count and component patch. */
fun ResultedRecipe.result(item: ItemArgument, count: Short? = null, init: (ComponentsPatch.() -> Unit)? = null) {
	result = CraftingResult(item.asId(), count, init?.let { ComponentsPatch().apply(it) })
}

/** Shared interface for recipes whose result is optional and defaults to the input item. */
interface TransmuteResultedRecipe {
	var result: TransmuteResult
}

/** Sets the result from an existing [ItemStack]. */
fun TransmuteResultedRecipe.result(itemStack: ItemStack) {
	result = TransmuteResult(itemStack.components?.toPatch(), itemStack.count, itemStack.id)
}

/** Sets the result using a [TransmuteResult] builder block. */
fun TransmuteResultedRecipe.result(block: TransmuteResult.() -> Unit) { result = TransmuteResult().apply(block) }

/** Sets the result to [item] with an optional count and component patch. */
fun TransmuteResultedRecipe.result(item: ItemArgument, count: Short? = null, init: (ComponentsPatch.() -> Unit)? = null) {
	result = TransmuteResult(init?.let { ComponentsPatch().apply(it) }, count, item.asId())
}
