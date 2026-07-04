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
 * Recipe for extending a map by combining it with paper.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_mapextending
 */
@Serializable
@SerialName("crafting_special_mapextending")
data class CraftingSpecialMapExtending(
	/** The map to extend. */
	var map: InlinableList<ItemOrTagArgument>,
	/** The paper ingredient used to extend the map. */
	var material: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("mapextending")
}

/**
 * Adds a `crafting_special_mapextending` recipe to the data pack.
 *
 * Use [CraftingSpecialMapExtending.map] and [CraftingSpecialMapExtending.material] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_mapextending
 */
fun Recipes.craftingSpecialMapExtending(name: String, block: CraftingSpecialMapExtending.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialMapExtending(
			map = listOf(),
			material = listOf(),
			result = CraftingResult(id = ""),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the map ingredient to one or more specific items. */
fun CraftingSpecialMapExtending.map(vararg items: ItemArgument) = apply { map = items.toList() }

/** Sets the map ingredient to an item tag. */
fun CraftingSpecialMapExtending.map(tag: ItemTagArgument) = apply { map = listOf(tag) }

/** Sets the paper material to one or more specific items. */
fun CraftingSpecialMapExtending.material(vararg items: ItemArgument) = apply { material = items.toList() }

/** Sets the paper material to an item tag. */
fun CraftingSpecialMapExtending.material(tag: ItemTagArgument) = apply { material = listOf(tag) }
