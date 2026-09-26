package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Recipe for smelting ores and materials in a blast furnace (twice as fast as a furnace).
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#blasting
 */
@Serializable
data class Blasting(
	override var ingredient: InlinableList<ItemOrTagArgument> = emptyList(),
	override var result: CraftingResult,
	override var category: CookingRecipeCategory? = null,
	override var group: String? = null,
	override var experience: Double? = null,
	@SerialName("cookingtime")
	override var cookingTime: Int? = null,
	var showNotification: Boolean? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.BLASTING
}

/**
 * Adds a `blasting` recipe to the data pack.
 *
 * Use [IngredientsRecipe.ingredient] and [ResultedRecipe.result] inside the block to configure the recipe.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#blasting
 */
fun Recipes.blasting(name: String, namespace: String? = null, block: Blasting.() -> Unit) =
	register(RecipeFile(name, Blasting(result = CraftingResult("")).apply(block)), namespace)
