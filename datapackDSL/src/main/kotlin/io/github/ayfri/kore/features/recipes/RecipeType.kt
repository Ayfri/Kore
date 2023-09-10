package io.github.ayfri.kore.features.recipes

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(RecipeType.Companion.RecipeTypeSerializer::class)
fun interface RecipeType {
	override fun toString(): String

	companion object {
		data object RecipeTypeSerializer : ToStringSerializer<RecipeType>()
	}
}

data object CraftingSpecial {
	operator fun invoke(group: String) = CraftingSpecialEntry(group)
}

@Serializable(RecipeType.Companion.RecipeTypeSerializer::class)
class CraftingSpecialEntry(group: String) : RecipeType {
	val group = group.lowercase().replace(' ', '_')
	override fun toString() = "crafting_special_$group"
}

object RecipeTypes {
	val BLASTING = RecipeType { "blasting" }
	val CAMPFIRE_COOKING = RecipeType { "campfire_cooking" }
	val CRAFTING_SHAPED = RecipeType { "crafting_shaped" }
	val CRAFTING_SHAPELESS = RecipeType { "crafting_shapeless" }
	val CRAFTING_SPECIAL = CraftingSpecial
	val SMELTING = RecipeType { "smelting" }
	val SMITHING_TRANSFORM = RecipeType { "smithing_transform" }
	val SMITHING_TRIM = RecipeType { "smithing_trim" }
	val SMOKING = RecipeType { "smoking" }
	val STONECUTTING = RecipeType { "stonecutting" }

	val values = listOf(
		BLASTING,
		CAMPFIRE_COOKING,
		CRAFTING_SHAPED,
		CRAFTING_SHAPELESS,
		CRAFTING_SPECIAL,
		SMELTING,
		SMITHING_TRANSFORM,
		SMITHING_TRIM,
		SMOKING,
		STONECUTTING,
	)
}
