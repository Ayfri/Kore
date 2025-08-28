package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.features.recipes.data.CraftingResult
import io.github.ayfri.kore.serializers.InlinableList

interface CookingRecipe {
	var ingredient: InlinableList<ItemOrTagArgument>
	var result: CraftingResult
	var group: String?
	var experience: Double?
	var cookingTime: Int?
}

fun CookingRecipe.ingredient(vararg items: ItemArgument) = apply { ingredient = items.toList() }
fun CookingRecipe.ingredient(tag: ItemOrTagArgument) = apply { ingredient = listOf(tag) }
