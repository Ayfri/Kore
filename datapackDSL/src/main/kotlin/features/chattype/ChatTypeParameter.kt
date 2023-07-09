package features.chattype

import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(with = ChatTypeParameter.Companion.ChatTypeParameterSerializer::class)
enum class ChatTypeParameter {
	TARGET,
	SENDER,
	CONTENT;

	companion object {
		data object ChatTypeParameterSerializer : LowercaseSerializer<ChatTypeParameter>(entries)
	}
}
