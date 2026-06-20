package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.ItemTagArgument

/** Shared interface for recipes that consume a single ingredient slot (items or item tags). */
interface IngredientsRecipe {
	var ingredient: List<ItemOrTagArgument>
}

/** Sets the ingredient to one or more specific items. */
fun IngredientsRecipe.ingredient(vararg items: ItemOrTagArgument) = apply { ingredient = items.toList() }

/** Sets the ingredient to an item tag. */
fun IngredientsRecipe.ingredient(tag: ItemTagArgument) = apply { ingredient = listOf(tag) }
