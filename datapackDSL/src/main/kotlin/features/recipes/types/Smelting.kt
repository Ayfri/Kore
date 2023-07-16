package features.recipes.types


import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.item
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import serializers.InlinableList

@Serializable
data class Smelting(
	override var ingredient: InlinableList<Ingredient>,
	override var result: ItemArgument,
	override var group: String? = null,
	override var experience: Double? = null,
	override var cookingTime: Int? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.SMELTING
}

fun Recipes.smelting(name: String, block: CookingRecipe.() -> Unit): RecipeArgument {
	dp.recipes.add(RecipeFile(name, Smelting(mutableListOf(), item("")).apply(block)))
	return RecipeArgument(name, dp.name)
}
