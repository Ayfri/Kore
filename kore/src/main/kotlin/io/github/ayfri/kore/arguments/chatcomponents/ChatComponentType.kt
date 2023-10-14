package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChatComponentType.Companion.ChatComponentTypeSerializer::class)
enum class ChatComponentType {
	TEXT,
	TRANSLATABLE,
	SCORE,
	SELECTOR,
	NBT,
	KEYBIND;

	companion object {
		data object ChatComponentTypeSerializer : LowercaseSerializer<ChatComponentType>(entries)
	}
}
