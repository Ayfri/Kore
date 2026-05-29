package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

@Serializable
data class ContentsEntityUUID(
	var id: String,
	var name: ChatComponent? = null,
	var uuid: String? = null,
) : Contents {
	override fun toNbtTag() = nbt {
		this["id"] = id
		name?.let { this["name"] = it.toNbtTag() }
		uuid?.let { this["uuid"] = it }
	}
}
