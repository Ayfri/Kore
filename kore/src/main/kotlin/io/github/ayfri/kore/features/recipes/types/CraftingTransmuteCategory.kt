package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CraftingTransmuteCategory.Companion.CraftingTransmuteCategorySerializer::class)
enum class CraftingTransmuteCategory {
	BUILDING,
	REDSTONE,
	EQUIPMENT,
	MISC;

	companion object {
		data object CraftingTransmuteCategorySerializer : LowercaseSerializer<CraftingTransmuteCategory>(entries)
	}
}
