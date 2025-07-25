package io.github.ayfri.kore.features.chattypes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.types.ChatTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChatType(
	@Transient
	override var fileName: String = "chat_type",
	var chat: ChatTypeDecoration = ChatTypeDecoration("chat.type.text"),
	var narration: ChatTypeDecoration = ChatTypeDecoration("chat.type.text.narrate"),
) : Generator("chat_type") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.chatType(
	fileName: String = "chat_type",
	block: ChatType.() -> Unit = {},
): ChatTypeArgument {
	val chatType = ChatType(fileName).apply(block)
	chatTypes += chatType
	return ChatTypeArgument(fileName, chatType.namespace ?: name)
}

fun ChatType.chat(translationKey: String = "chat.type.text", block: ChatTypeDecoration.() -> Unit = {}) {
	chat = ChatTypeDecoration(translationKey).apply(block)
}

fun ChatType.narration(translationKey: String = "chat.type.text.narrate", block: ChatTypeDecoration.() -> Unit = {}) {
	narration = ChatTypeDecoration(translationKey).apply(block)
}
