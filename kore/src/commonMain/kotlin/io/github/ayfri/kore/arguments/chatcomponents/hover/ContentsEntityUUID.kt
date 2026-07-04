package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

/**
 * [HoverAction.SHOW_ENTITY] payload where the UUID is represented as a hyphenated string (e.g. `"550e8400-e29b-41d4-a716-446655440000"`).
 *
 * Use [ContentsEntityIntArray] instead when the UUID is stored as a 4-element int array.
 */
@Serializable
data class ContentsEntityUUID(
	/** Namespaced entity type ID (e.g. `"minecraft:zombie"`). */
	var id: String,
	/** Optional display name shown above the entity type in the tooltip. */
	var name: ChatComponent? = null,
	/** UUID of the entity as a hyphenated string. */
	var uuid: String? = null,
) : Contents {
	override fun toNbtTag() = nbt {
		this["id"] = id
		name?.let { this["name"] = it.toNbtTag() }
		uuid?.let { this["uuid"] = it }
	}
}
