package features.recipes.types

import features.recipes.RecipeTypes
import features.recipes.data.CraftingResult
import features.recipes.data.Ingredients
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShaped(
	override var group: String? = null,
	val pattern: MutableList<String> = mutableListOf(),
	val key: MutableMap<String, Ingredients> = mutableMapOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPED
}
