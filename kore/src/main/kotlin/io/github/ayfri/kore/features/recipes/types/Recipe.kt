package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.features.recipes.RecipeType
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Recipe.Companion.RecipeSerializer::class)
sealed class Recipe {
	abstract val type: RecipeType
	abstract var group: String?

	companion object {
		data object RecipeSerializer : NamespacedPolymorphicSerializer<Recipe>(Recipe::class)
	}
}
