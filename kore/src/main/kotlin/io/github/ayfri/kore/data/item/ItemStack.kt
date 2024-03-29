package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	val id: String,
	val count: Short? = null,
	@Serializable(Components.Companion.ComponentsSerializer::class) val components: Components? = null,
) {
	fun toItemArgument() = ItemArgument(id.substringBefore(":"), id.substringAfter(":"), components)
}
