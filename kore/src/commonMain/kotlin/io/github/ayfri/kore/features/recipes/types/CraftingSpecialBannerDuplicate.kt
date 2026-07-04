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
 * Recipe that duplicates a banner pattern onto another banner.
 *
 * Produces `data/<namespace>/recipe/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_bannerduplicate
 */
@Serializable
@SerialName("crafting_special_bannerduplicate")
data class CraftingSpecialBannerDuplicate(
	/** The banner to copy the pattern from. */
	var banner: InlinableList<ItemOrTagArgument>,
	override var result: CraftingResult,
) : Recipe(), ResultedRecipe {
	override val type = RecipeTypes.CRAFTING_SPECIAL("bannerduplicate")
}

/**
 * Adds a `crafting_special_bannerduplicate` recipe to the data pack.
 *
 * Use [CraftingSpecialBannerDuplicate.banner] inside the block to set the source banner ingredient.
 * Produces `data/<namespace>/recipe/<name>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_special_bannerduplicate
 */
fun Recipes.craftingSpecialBannerDuplicate(
	name: String,
	block: CraftingSpecialBannerDuplicate.() -> Unit
): RecipeArgument {
	val recipe = RecipeFile(
		name, CraftingSpecialBannerDuplicate(
			banner = listOf(),
			result = CraftingResult(id = ""),
		).apply(block)
	)
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}

/** Sets the source banner ingredient to one or more specific items. */
fun CraftingSpecialBannerDuplicate.banner(vararg items: ItemArgument) = apply { banner = items.toList() }

/** Sets the source banner ingredient to an item tag. */
fun CraftingSpecialBannerDuplicate.banner(tag: ItemTagArgument) = apply { banner = listOf(tag) }
