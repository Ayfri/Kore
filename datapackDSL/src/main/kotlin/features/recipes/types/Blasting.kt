package features.recipes.types

import arguments.Argument
import features.recipes.RecipeTypes
import features.recipes.data.Ingredients
import kotlinx.serialization.Serializable

@Serializable
data class Blasting(
	override val ingredient: Ingredients,
	override var result: Argument.Item,
	override var group: String? = null,
	override var experience: Double? = null,
	override var cookingTime: Int? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.BLASTING
}
