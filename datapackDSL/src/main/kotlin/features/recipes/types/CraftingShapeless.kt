package features.recipes.types

import arguments.Argument
import arguments.item
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.CraftingResult
import features.recipes.data.Ingredient
import features.recipes.data.Ingredients
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShapeless(
	override var group: String? = null,
	val ingredient: Ingredients = mutableListOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPELESS
}

fun Recipes.craftingShapeless(name: String, block: CraftingShapeless.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, CraftingShapeless(result = CraftingResult(item = item(""))).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun CraftingShapeless.ingredient(block: Ingredient.() -> Unit) = ingredient.add(Ingredient().apply(block))
fun CraftingShapeless.ingredient(item: Argument.Item? = null, tag: Argument.ItemTag? = null) = ingredient.add(Ingredient(item, tag))
