package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChatComponentType.Companion.ChatComponentTypeSerializer::class)
enum class ChatComponentType {
	KEYBIND,
	NBT,
	OBJECT,
	SCORE,
	SELECTOR,
	TEXT,
	TRANSLATABLE,
	;

	companion object {
		data object ChatComponentTypeSerializer : LowercaseSerializer<ChatComponentType>(entries)
	}
}
