package features.recipes.types

import features.recipes.RecipeTypes
import features.recipes.data.Ingredient
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SmithingTrim(
	var template: Ingredient,
	var base: Ingredient,
	var addition: Ingredient,
) : Recipe() {
	override val type = RecipeTypes.SMITHING_TRANSFORM

	@Transient
	override var group: String? = null
}
