package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.Ingredient
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

@Serializable
data class CraftingShapeless(
	override var group: String? = null,
	var ingredients: List<Ingredient> = mutableListOf(),
	override var result: ItemStack,
) : Recipe(), CraftingRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPELESS
}

fun Recipes.craftingShapeless(name: String, block: CraftingShapeless.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, CraftingShapeless(result = ItemStack(item(""))).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

fun CraftingShapeless.ingredient(block: Ingredient.() -> Unit) = Ingredient().apply(block).also { ingredients += it }
fun CraftingShapeless.ingredient(item: ItemArgument? = null, tag: ItemTagArgument? = null) =
	Ingredient(item, tag).also { ingredients += it }
