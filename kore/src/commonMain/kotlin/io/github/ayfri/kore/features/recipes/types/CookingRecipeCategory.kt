package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Category for cooking recipes, controlling placement in the furnace, blast furnace and smoker recipe books.
 *
 * Used by [Blasting], [CampfireCooking], [Smelting] and [Smoking] recipes.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe
 */
@Serializable(with = CookingRecipeCategory.Companion.CookingRecipeCategorySerializer::class)
enum class CookingRecipeCategory {
	/** Smeltable blocks like sand or cobblestone. */
	BLOCKS,

	/** Cookable food. */
	FOOD,

	/** Everything that doesn't fit another category. */
	MISC;

	companion object {
		data object CookingRecipeCategorySerializer : LowercaseSerializer<CookingRecipeCategory>(entries)
	}
}
