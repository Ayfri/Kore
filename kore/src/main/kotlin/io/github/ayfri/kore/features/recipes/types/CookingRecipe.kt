package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList

interface CookingRecipe : IngredientsRecipe, ResultedRecipe {
	override var ingredient: InlinableList<ItemOrTagArgument>
	var group: String?
	var experience: Double?
	var cookingTime: Int?
}
