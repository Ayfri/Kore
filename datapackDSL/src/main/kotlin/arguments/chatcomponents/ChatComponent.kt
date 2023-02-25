package arguments.chatcomponents

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound

@Serializable
sealed interface ChatComponent {
	fun toNbtTag(): NbtCompound
}
