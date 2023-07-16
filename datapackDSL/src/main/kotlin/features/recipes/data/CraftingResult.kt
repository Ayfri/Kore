package features.recipes.data

import arguments.types.resources.ItemArgument
import kotlinx.serialization.Serializable

@Serializable
data class CraftingResult(
	var item: ItemArgument,
	var count: Int? = null,
)
