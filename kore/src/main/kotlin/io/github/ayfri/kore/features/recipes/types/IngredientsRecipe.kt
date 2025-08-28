package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument

interface IngredientsRecipe {
	var ingredient: List<ItemOrTagArgument>
}

fun IngredientsRecipe.ingredient(vararg items: ItemOrTagArgument) = apply { ingredient = items.toList() }
fun IngredientsRecipe.ingredient(tag: ItemTagArgument) = apply { ingredient = listOf(tag) }
