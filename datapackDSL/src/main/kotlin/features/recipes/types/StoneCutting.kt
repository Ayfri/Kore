package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.item
import arguments.types.resources.tagged.ItemTagArgument
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredient
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class StoneCutting(
	override var group: String? = null,
	var ingredient: InlinableList<Ingredient> = emptyList(),
	var result: ItemArgument,
	var count: Int,
) : Recipe() {
	override val type = RecipeTypes.STONECUTTING
}

fun Recipes.stoneCutting(name: String, block: StoneCutting.() -> Unit): RecipeArgument {
	dp.recipes += RecipeFile(name, StoneCutting(result = item(""), count = 1).apply(block))
	return RecipeArgument(name, dp.name)
}

fun StoneCutting.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredient += it }
fun StoneCutting.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) = Ingredient(item, tag).also { ingredient += it }
