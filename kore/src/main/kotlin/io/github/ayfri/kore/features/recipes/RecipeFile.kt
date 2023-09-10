package io.github.ayfri.kore.features.recipes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.recipes.types.Recipe
import kotlinx.serialization.encodeToString

data class RecipeFile(
	override var fileName: String = "recipe",
	var recipe: Recipe,
) : Generator("recipe") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(recipe)
}

val DataPack.recipesBuilder get() = Recipes(this)
fun DataPack.recipes(block: Recipes.() -> Unit) = recipesBuilder.apply(block)
