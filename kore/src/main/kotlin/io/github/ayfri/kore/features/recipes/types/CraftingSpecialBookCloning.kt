package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
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
 * Recipe that copies a written book, optionally restricting how many generation copies are allowed.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_bookcloning
 */
@Serializable
@SerialName("crafting_special_bookcloning")
data class CraftingSpecialBookCloning(
	/** Allowed generation values of the source book; omit to allow any generation. */
	var allowedGenerations: IntRangeOrIntJson? = null,
	/** The blank book(s) to write the copy into. */
	var material: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
	/** The written book to copy from. */
	var source: InlinableList<ItemOrTagArgument>,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("bookcloning")
}

/**
 * Adds a `crafting_special_bookcloning` recipe to the data pack.
 *
 * Use [CraftingSpecialBookCloning.source] and [CraftingSpecialBookCloning.material] inside the block.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_bookcloning
 */
fun Recipes.craftingSpecialBookCloning(name: String, block: CraftingSpecialBookCloning.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialBookCloning(
			material = listOf(),
			result = CraftingResult(id = ""),
			source = listOf(),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the material (blank books) to one or more specific items. */
fun CraftingSpecialBookCloning.material(vararg items: ItemArgument) = apply { material = items.toList() }

/** Sets the material (blank books) to an item tag. */
fun CraftingSpecialBookCloning.material(tag: ItemTagArgument) = apply { material = listOf(tag) }

/** Sets the source (book to copy) to one or more specific items. */
fun CraftingSpecialBookCloning.source(vararg items: ItemArgument) = apply { source = items.toList() }

/** Sets the source (book to copy) to an item tag. */
fun CraftingSpecialBookCloning.source(tag: ItemTagArgument) = apply { source = listOf(tag) }
