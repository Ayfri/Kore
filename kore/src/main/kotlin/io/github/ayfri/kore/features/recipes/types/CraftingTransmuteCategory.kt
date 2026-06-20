package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * Category for [CraftingTransmute] recipes, controlling placement in the recipe book.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#crafting_transmute
 */
@Serializable(with = CraftingTransmuteCategory.Companion.CraftingTransmuteCategorySerializer::class)
enum class CraftingTransmuteCategory {
	/** Blocks and structural items. */
	BUILDING,

	/** Redstone components and mechanisms. */
	REDSTONE,

	/** Tools, weapons, and armor. */
	EQUIPMENT,

	/** Everything that doesn't fit another category. */
	MISC;

	companion object {
		data object CraftingTransmuteCategorySerializer : LowercaseSerializer<CraftingTransmuteCategory>(entries)
	}
}
