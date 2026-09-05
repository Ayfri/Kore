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
 * Stonecutter recipe that converts a block into one or more different blocks or slabs.
 *
 * Multiple stonecutting recipes can share the same ingredient to offer different outputs.
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#stonecutting
 */
@Serializable
data class StoneCutting(
	override var ingredient: InlinableList<ItemOrTagArgument> = emptyList(),
	override var result: CraftingResult,
	var showNotification: Boolean? = null,
) : Recipe(), IngredientsRecipe, ResultedRecipe {
	override val type = RecipeTypes.STONECUTTING
}

/**
 * Adds a `stonecutting` recipe to the data pack.
 *
 * Use [IngredientsRecipe.ingredient] and [ResultedRecipe.result] inside the block to configure the recipe.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#stonecutting
 */
fun Recipes.stoneCutting(name: String, block: StoneCutting.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, StoneCutting(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}
