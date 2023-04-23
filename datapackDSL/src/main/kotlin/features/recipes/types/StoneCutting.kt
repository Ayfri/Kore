package features.recipes.types

import arguments.Argument
import features.recipes.RecipeTypes
import features.recipes.data.Ingredients
import kotlinx.serialization.Serializable

@Serializable
data class StoneCutting(
	override var group: String? = null,
	var ingredient: Ingredients = mutableListOf(),
	var result: Argument.Item,
	var count: Int,
) : Recipe() {
	override val type = RecipeTypes.STONECUTTING
}
