package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

/**
 * [HoverAction.SHOW_ENTITY] payload where the UUID is represented as a 4-element int array (NBT `[I;...]` format).
 *
 * Use [ContentsEntityUUID] instead when the UUID is available as a hyphenated string.
 */
@Serializable
data class ContentsEntityIntArray(
	/** Namespaced entity type ID (e.g. `"minecraft:zombie"`). */
	var id: String,
	/** Optional display name shown above the entity type in the tooltip. */
	var name: ChatComponent? = null,
	/** UUID of the entity as a 4-element signed int array. */
	var uuid: IntArray? = null,
) : Contents {
	override fun toNbtTag() = nbt {
		this["id"] = id
		name?.let { this["name"] = it.toNbtTag() }
		uuid?.let { this["uuid"] = it }
	}
}
