package data.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
data class ItemStack(
	val id: String,
	@SerialName("Count")
	val count: Short? = null,
	val tag: NbtCompound? = null,
)
