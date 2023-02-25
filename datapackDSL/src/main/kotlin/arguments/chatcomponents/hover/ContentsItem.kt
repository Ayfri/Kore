package arguments.chatcomponents.hover

import arguments.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

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
