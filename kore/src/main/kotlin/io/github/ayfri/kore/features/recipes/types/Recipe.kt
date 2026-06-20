package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.features.recipes.RecipeType
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

/**
 * Base class for all data-driven recipe types.
 *
 * Subclasses are serialized polymorphically: the `type` field is written as `minecraft:<snake_case_class_name>`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/recipes
 * Minecraft Wiki: https://minecraft.wiki/w/Recipe
 */
@Serializable(with = Recipe.Companion.RecipeSerializer::class)
sealed class Recipe {
	abstract val type: RecipeType
	open var group: String? = null

	companion object {
		data object RecipeSerializer : NamespacedPolymorphicSerializer<Recipe>(Recipe::class)
	}
}
