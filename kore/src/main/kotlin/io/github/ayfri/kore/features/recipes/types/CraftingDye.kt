package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Recipe that dyes an item using a dye ingredient.
 *
 * The item in `target` is dyed with the item in `dye` and produces `result`.
 * Replaces the removed `minecraft:crafting_special_armordye` special recipe.
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_dye
 */
@Serializable
data class CraftingDye(
	/** Optional recipe book category. */
	var category: RecipeCategory? = null,
	/** The dye ingredient(s) to apply. */
	var dye: InlinableList<ItemOrTagArgument>,
	/** Optional recipe book group name. */
	override var group: String? = null,
	/** The output item. */
	override var result: CraftingResult,
	/** Whether to show the recipe unlock notification. */
	@SerialName("show_notification") var showNotification: Boolean? = null,
	/** The item(s) to be dyed. */
	var target: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_DYE
}

/**
 * Adds a `crafting_dye` recipe to the data pack.
 *
 * Use [CraftingDye.dye] and [CraftingDye.target] inside the block to set the ingredients.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_dye
 */
fun Recipes.craftingDye(name: String, block: CraftingDye.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingDye(
			dye = listOf(),
			result = CraftingResult(id = ""),
			target = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the dye ingredient(s) to one or more specific items. */
fun CraftingDye.dye(vararg items: ItemArgument) = apply { dye = items.toList() }

/** Sets the dye ingredient to an item tag. */
fun CraftingDye.dye(tag: ItemTagArgument) = apply { dye = listOf(tag) }

/** Sets the target (item to be dyed) to one or more specific items. */
fun CraftingDye.target(vararg items: ItemArgument) = apply { target = items.toList() }

/** Sets the target (item to be dyed) to an item tag. */
fun CraftingDye.target(tag: ItemTagArgument) = apply { target = listOf(tag) }
