package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.buildNbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class ContentsEntityIntArray(
	var type: String,
	var name: ChatComponent? = null,
	var id: IntArray? = null,
) : Contents {
	override fun toNbtTag() = buildNbtCompound {
		this["type"] = type
		name?.let { this["name"] = it.toNbtTag() }
		id?.let { this["id"] = it }
	}
}
