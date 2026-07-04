package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Category for crafting recipes, controlling placement in the recipe book.
 *
 * Used by [CraftingDye] and [CraftingImbue] recipes.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe
 */
@Serializable(with = RecipeCategory.Companion.RecipeCategorySerializer::class)
enum class RecipeCategory {
	/** Blocks and structural items. */
	BUILDING,

	/** Tools, weapons, and armor. */
	EQUIPMENT,

	/** Everything that doesn't fit another category. */
	MISC,

	/** Redstone components and mechanisms. */
	REDSTONE;

	companion object {
		data object RecipeCategorySerializer : LowercaseSerializer<RecipeCategory>(entries)
	}
}
