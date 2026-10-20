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

data object RecipeTypes {
	val BLASTING = RecipeType { "blasting" }
	val BREWING = RecipeType { "brewing" }
	val CAMPFIRE_COOKING = RecipeType { "campfire_cooking" }
	val CRAFTING_DECORATED_POT = RecipeType { "crafting_decorated_pot" }
	val CRAFTING_DYE = RecipeType { "crafting_dye" }
	val CRAFTING_IMBUE = RecipeType { "crafting_imbue" }
	val CRAFTING_SHAPED = RecipeType { "crafting_shaped" }
	val CRAFTING_SHAPELESS = RecipeType { "crafting_shapeless" }
	val CRAFTING_TRANSMUTE = RecipeType { "crafting_transmute" }
	val SMELTING = RecipeType { "smelting" }
	val SMITHING_TRANSFORM = RecipeType { "smithing_transform" }
	val SMITHING_TRIM = RecipeType { "smithing_trim" }
	val SMOKING = RecipeType { "smoking" }
	val STONECUTTING = RecipeType { "stonecutting" }

	/** `RecipeTypes.CRAFTING_SPECIAL("book cloning")` is `crafting_special_book_cloning`. */
	@Suppress("FunctionName")
	fun CRAFTING_SPECIAL(group: String) = RecipeType { "crafting_special_${group.lowercase().replace(' ', '_')}" }
}
