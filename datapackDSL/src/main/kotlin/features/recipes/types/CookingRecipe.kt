package features.recipes.types

import arguments.Argument
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import serializers.SingleOrMultiSerializer

interface CookingRecipe {
	@Serializable(SingleOrMultiSerializer::class)
	val ingredient: MutableList<Ingredient>
	var result: Argument.Item
	var group: String?
	var experience: Double?
	var cookingTime: Int?
}
