package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.buildNbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class ContentsItem(
	var id: String,
	var count: Int? = null,
	var tag: String? = null,
) : Contents {
	override fun toNbtTag() = buildNbtCompound {
		this["id"] = id
		count?.let { this["count"] = it }
		tag?.let { this["tag"] = it }
	}
}
