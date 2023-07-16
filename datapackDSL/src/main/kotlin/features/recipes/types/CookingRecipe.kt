package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.tagged.ItemTagArgument
import features.recipes.data.Ingredient
import serializers.InlinableList

interface CookingRecipe {
	var ingredient: InlinableList<Ingredient>
	var result: ItemArgument
	var group: String?
	var experience: Double?
	var cookingTime: Int?
}

fun CookingRecipe.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredient += it }
fun CookingRecipe.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) = Ingredient(item, tag).also { ingredient += it }
