package io.github.ayfri.kore.features.recipes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.recipes.types.Recipe
import kotlinx.serialization.encodeToString

/**
 * Data-driven recipe definition.
 *
 * Recipes are used to craft items, blocks, and other game elements. They can be crafted in
 * crafting tables, smelters, and other crafting stations.
 *
 * JSON format reference: https://minecraft.wiki/w/Recipe
 * Docs: https://kore.ayfri.com/docs/recipes
 *
 * @param recipe - The recipe to be defined.
 */
data class RecipeFile(
	override var fileName: String = "recipe",
	var recipe: Recipe,
) : Generator("recipe") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(recipe)
}

val DataPack.recipesBuilder get() = Recipes(this)

/**
 * Declare recipes using Kore's DSL builder.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/recipes
 */
fun DataPack.recipes(block: Recipes.() -> Unit) = recipesBuilder.apply(block)
