package features.recipes

import DataPack
import Generator
import features.recipes.types.Recipe
import kotlinx.serialization.encodeToString

data class RecipeFile(
	override var fileName: String = "recipe",
	var recipe: Recipe,
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(recipe)
}

val DataPack.recipesBuilder get() = Recipes(this)
fun DataPack.recipes(block: Recipes.() -> Unit) = recipesBuilder.apply(block)
