package data.item

import arguments.types.resources.ItemArgument
import net.benwoodworth.knbt.NbtCompound
import serializers.JsonSerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	val id: String,
	@JsonSerialName("Count")
	val count: Short? = null,
	val tag: NbtCompound? = null,
) {
	fun toItemArgument() = ItemArgument(id.substringBefore(":"), id.substringAfter(":"), tag)
}
