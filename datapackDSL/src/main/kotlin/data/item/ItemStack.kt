package data.item

import net.benwoodworth.knbt.NbtCompound
import serializers.JsonSerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemStack(
	val id: String,
	@JsonSerialName("Count")
	val count: Short? = null,
	val tag: NbtCompound? = null,
)
