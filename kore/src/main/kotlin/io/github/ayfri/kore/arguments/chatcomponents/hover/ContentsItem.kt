package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.components.ComponentsRemovables
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class ContentsItem(
	var id: String,
	var count: Int? = null,
	var components: ComponentsRemovables? = null,
) : Contents {
	override fun toNbtTag() = buildNbtCompound {
		this["id"] = id
		count?.let { this["count"] = it }
		components?.let { this["components"] = it.asNbt() }
	}
}
