package features.recipes.data

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable
data class CraftingResult(
	var item: Argument.Item,
	var count: Int? = null,
)
