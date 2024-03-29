package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.features.recipes.data.Ingredient
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShapeless(
	override var group: String? = null,
	var ingredients: List<Ingredient> = mutableListOf(),
	override var result: CraftingResult,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPELESS
}

fun Recipes.craftingShapeless(name: String, block: CraftingShapeless.() -> Unit): RecipeArgument {
	dp.recipes += RecipeFile(name, CraftingShapeless(result = CraftingResult(id = item(""))).apply(block))
	return RecipeArgument(name, dp.name)
}

fun CraftingShapeless.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredients += it }
fun CraftingShapeless.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) =
	Ingredient(item, tag).also { ingredients += it }
