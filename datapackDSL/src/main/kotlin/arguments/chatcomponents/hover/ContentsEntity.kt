package arguments.chatcomponents.hover

import arguments.chatcomponents.ChatComponent
import arguments.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class ContentsEntity(
	var type: String,
	var name: ChatComponent? = null,
	var id: String? = null,
) : Contents {
	override fun toNbtTag() = buildNbtCompound {
		this["type"] = type
		name?.let { this["name"] = it.toNbtTag() }
		id?.let { this["id"] = it }
	}
}
