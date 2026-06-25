package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

/** [HoverAction.SHOW_ITEM] payload that renders a full item tooltip including name, lore, and enchantments. */
@Serializable
data class ContentsItem(
	/** Namespaced item ID (e.g. `"minecraft:diamond_sword"`). */
	var id: String,
	/** Stack count displayed in the tooltip. */
	var count: Int? = null,
	/** Item component overrides applied on top of the item's defaults (e.g. custom name, enchantments). */
	var components: ComponentsPatch? = null,
) : Contents {
	override fun toNbtTag() = nbt {
		this["id"] = id
		count?.let { this["count"] = it }
		components?.let { this["components"] = it.asNbt() }
	}
}
