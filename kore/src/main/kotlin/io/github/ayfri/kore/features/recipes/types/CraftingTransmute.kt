package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.Ingredient
import kotlinx.serialization.Serializable

@Serializable
data class CraftingTransmute(
	override var group: String? = null,
	var category: CraftingTransmuteCategory? = null,
	var input: Ingredient,
	var material: Ingredient,
	override var result: ItemStack,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_TRANSMUTE
}

fun Recipes.craftingTransmute(name: String, block: CraftingTransmute.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingTransmute(
			input = Ingredient(),
			material = Ingredient(),
			result = itemStack(item = item("")),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun CraftingTransmute.input(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { input = it }

fun CraftingTransmute.material(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { material = it }
