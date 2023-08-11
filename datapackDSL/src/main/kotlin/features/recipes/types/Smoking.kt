package features.recipes.types

import arguments.types.resources.ItemArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.item
import features.recipes.RecipeFile
import features.recipes.RecipeTypes
import features.recipes.Recipes
import features.recipes.data.Ingredient
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Smoking(
	override var ingredient: InlinableList<Ingredient> = emptyList(),
	override var result: ItemArgument,
	override var group: String? = null,
	override var experience: Double? = null,
	override var cookingTime: Int? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.SMOKING
}

fun Recipes.smoking(name: String, block: CookingRecipe.() -> Unit): RecipeArgument {
	dp.recipes += RecipeFile(name, Smoking(result = item("")).apply(block))
	return RecipeArgument(name, dp.name)
}
