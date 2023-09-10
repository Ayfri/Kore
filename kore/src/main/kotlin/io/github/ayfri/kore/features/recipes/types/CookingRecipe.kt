package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.data.Ingredient
import io.github.ayfri.kore.serializers.InlinableList

interface CookingRecipe {
	var ingredient: InlinableList<Ingredient>
	var result: ItemArgument
	var group: String?
	var experience: Double?
	var cookingTime: Int?
}

fun CookingRecipe.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredient += it }
fun CookingRecipe.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) = Ingredient(item, tag).also { ingredient += it }
