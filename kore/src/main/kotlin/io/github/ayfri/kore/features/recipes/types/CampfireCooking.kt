package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Recipe for cooking food items on a campfire (slow, no fuel required).
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#campfire_cooking
 */
@Serializable
data class CampfireCooking(
	override var ingredient: InlinableList<ItemOrTagArgument> = emptyList(),
	override var result: CraftingResult,
	override var group: String? = null,
	override var experience: Double? = null,
	override var cookingTime: Int? = null,
	var showNotification: Boolean? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.CAMPFIRE_COOKING
}

/**
 * Adds a `campfire_cooking` recipe to the data pack.
 *
 * Use [IngredientsRecipe.ingredient] and [ResultedRecipe.result] inside the block to configure the recipe.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#campfire_cooking
 */
fun Recipes.campfireCooking(name: String, block: CookingRecipe.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, CampfireCooking(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}
