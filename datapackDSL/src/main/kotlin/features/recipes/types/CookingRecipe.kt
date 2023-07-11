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

fun CookingRecipe.ingredient(block: Ingredient.() -> Unit) = ingredient.add(Ingredient().apply(block))
fun CookingRecipe.ingredient(item: Argument.Item? = null, tag: Argument.ItemTag? = null) = ingredient.add(Ingredient(item, tag))
