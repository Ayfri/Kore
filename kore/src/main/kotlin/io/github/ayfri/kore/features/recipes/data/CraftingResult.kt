package io.github.ayfri.kore.features.recipes.data

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import kotlinx.serialization.Serializable

@Serializable
data class CraftingResult(
	var id: ItemArgument,
	var count: Int? = null,
	var components: Components? = null,
)

fun CraftingResult.components(block: Components.() -> Unit) = Components().apply(block).also { components = it }
