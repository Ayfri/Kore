package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.features.recipes.data.CraftingResult


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
