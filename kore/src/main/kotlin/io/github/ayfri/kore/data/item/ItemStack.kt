package io.github.ayfri.kore.data.item

import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	val id: String,
	@JsonSerialName("Count")
	val count: Short? = null,
	val components: Components? = null,
) {
	fun toItemArgument() = ItemArgument(id.substringBefore(":"), id.substringAfter(":"), components)
}
