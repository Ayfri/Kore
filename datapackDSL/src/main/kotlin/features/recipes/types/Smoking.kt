package features.recipes.types


import arguments.Argument
import arguments.item
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredients
import kotlinx.serialization.Serializable

@Serializable
data class Smoking(
	override val ingredient: Ingredients,
	override var result: Argument.Item,
	override var group: String? = null,
	override var experience: Double? = null,
	override var cookingTime: Int? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.SMOKING
}

fun Recipes.smoking(name: String, block: CookingRecipe.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, Smoking(mutableListOf(), item("")).apply(block)))
	return Argument.Recipe(name, dp.name)
}
