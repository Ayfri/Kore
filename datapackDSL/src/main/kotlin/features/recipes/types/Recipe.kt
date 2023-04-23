package features.recipes.types

import features.recipes.RecipeType
import kotlinx.serialization.Serializable

@Serializable
sealed class Recipe {
	abstract val type: RecipeType
	abstract var group: String?
}
