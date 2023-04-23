package features.recipes.types

import arguments.Argument
import features.recipes.RecipeTypes
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable

@Serializable
data class Smithing(
	override var group: String? = null,
	var base: Ingredient,
	var addition: Ingredient,
	var result: Argument.Item,
) : Recipe() {
	override val type = RecipeTypes.SMITHING
}
