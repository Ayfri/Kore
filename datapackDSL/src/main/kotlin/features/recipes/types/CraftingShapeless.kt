package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.item
import arguments.types.resources.tagged.ItemTagArgument
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.CraftingResult
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShapeless(
	override var group: String? = null,
	var ingredient: List<Ingredient> = mutableListOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPELESS
}

fun Recipes.craftingShapeless(name: String, block: CraftingShapeless.() -> Unit): RecipeArgument {
	dp.recipes.add(RecipeFile(name, CraftingShapeless(result = CraftingResult(item = item(""))).apply(block)))
	return RecipeArgument(name, dp.name)
}

fun CraftingShapeless.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredient += it }
fun CraftingShapeless.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) = Ingredient(item, tag).also { ingredient += it }
