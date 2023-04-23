package features.recipes.data

import arguments.Argument
import kotlinx.serialization.Serializable
import serializers.SingleOrMultiSerializer

typealias Ingredients = @Serializable(with = SingleOrMultiSerializer::class) MutableList<Ingredient>

@Serializable
data class Ingredient(
	var item: Argument.Item? = null,
	var tag: Argument.ItemTag? = null,
)
