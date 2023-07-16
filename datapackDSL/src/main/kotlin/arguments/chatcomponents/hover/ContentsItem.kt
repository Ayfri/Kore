package arguments.chatcomponents.hover

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import utils.set

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
