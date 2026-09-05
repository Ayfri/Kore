package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Order-independent crafting table recipe where any arrangement of ingredients produces the result.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_shapeless
 */
@Serializable
data class CraftingShapeless(
	override var group: String? = null,
	/** Each entry is one ingredient slot; call [ingredient] once per required item. */
	var ingredients: List<InlinableList<ItemOrTagArgument>> = listOf(),
	override var result: CraftingResult,
	var showNotification: Boolean? = null,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SHAPELESS
}

/**
 * Adds a `crafting_shapeless` recipe to the data pack.
 *
 * Call [CraftingShapeless.ingredient] once per required ingredient slot inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_shapeless
 */
fun Recipes.craftingShapeless(name: String, block: CraftingShapeless.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, CraftingShapeless(result = CraftingResult("")).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Adds one ingredient slot accepting any of the given items. */
fun CraftingShapeless.ingredient(vararg items: ItemOrTagArgument) = apply { ingredients += listOf(items.toList()) }

/** Adds one ingredient slot accepting any item from the given tag. */
fun CraftingShapeless.ingredient(tag: ItemTagArgument) = apply { ingredients += listOf(listOf(tag)) }
