package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

@Serializable
data class ContentsItem(
	var id: String,
	var count: Int? = null,
	var components: ComponentsPatch? = null,
) : Contents {
	override fun toNbtTag() = nbt {
		this["id"] = id
		count?.let { this["count"] = it }
		components?.let { this["components"] = it.asNbt() }
	}
}
