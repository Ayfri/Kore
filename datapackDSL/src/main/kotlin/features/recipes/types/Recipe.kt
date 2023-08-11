package features.recipes.types

import features.recipes.RecipeType
import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Recipe.Companion.RecipeSerializer::class)
sealed class Recipe {
	abstract val type: RecipeType
	abstract var group: String?

	companion object {
		data object RecipeSerializer : NamespacedPolymorphicSerializer<Recipe>(Recipe::class)
	}
}
