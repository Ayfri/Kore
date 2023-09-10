package io.github.ayfri.kore.features.chattype

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChatTypeParameter.Companion.ChatTypeParameterSerializer::class)
enum class ChatTypeParameter {
	TARGET,
	SENDER,
	CONTENT;

	companion object {
		data object ChatTypeParameterSerializer : LowercaseSerializer<ChatTypeParameter>(entries)
	}
}
