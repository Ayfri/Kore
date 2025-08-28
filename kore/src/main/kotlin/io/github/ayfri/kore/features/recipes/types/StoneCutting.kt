package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class StoneCutting(
	override var group: String? = null,
	override var ingredient: InlinableList<ItemOrTagArgument> = emptyList(),
	override var result: CraftingResult,
) : Recipe(), IngredientsRecipe, ResultedRecipe {
	override val type = RecipeTypes.STONECUTTING
}

fun Recipes.stoneCutting(name: String, block: StoneCutting.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, StoneCutting(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}
