package features.recipes.types

import features.recipes.RecipeTypes
import features.recipes.data.CraftingResult
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
