package arguments.chatcomponents.hover

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag

@Serializable
sealed interface Contents {
	fun toNbtTag(): NbtTag
}
