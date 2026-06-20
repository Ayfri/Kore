package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList

/**
 * Shared interface for furnace-type cooking recipes (smelting, blasting, smoking, campfire cooking).
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe#Cooking
 */
interface CookingRecipe : IngredientsRecipe, ResultedRecipe {
	override var ingredient: InlinableList<ItemOrTagArgument>

	/** Optional recipe book group name. */
	var group: String?

	/** XP awarded when collecting the output. */
	var experience: Double?

	/** Processing time in ticks. */
	var cookingTime: Int?
}
