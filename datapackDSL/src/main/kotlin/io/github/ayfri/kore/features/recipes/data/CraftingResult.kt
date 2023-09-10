package io.github.ayfri.kore.features.recipes.data

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import kotlinx.serialization.Serializable

@Serializable
data class CraftingResult(
	var item: ItemArgument,
	var count: Int? = null,
)
