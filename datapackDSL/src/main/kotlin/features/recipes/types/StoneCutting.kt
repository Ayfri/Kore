package features.recipes.types

import arguments.Argument
import arguments.item
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredient
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

fun Recipes.stoneCutting(name: String, block: StoneCutting.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, StoneCutting(ingredient = mutableListOf(), result = item(""), count = 1).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun StoneCutting.ingredient(block: Ingredient.() -> Unit) = ingredient.add(Ingredient().apply(block))
fun StoneCutting.ingredient(item: Argument.Item? = null, tag: Argument.ItemTag? = null) = ingredient.add(Ingredient(item, tag))
