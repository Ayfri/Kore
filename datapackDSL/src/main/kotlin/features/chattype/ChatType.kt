package features.chattype

import DataPack
import Generator
import arguments.types.resources.ChatTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString

@Serializable
data class ChatType(
	@Transient
	override var fileName: String = "chat_type",
	var chat: ChatTypeDecoration = ChatTypeDecoration("chat.type.text"),
	var narration: ChatTypeDecoration = ChatTypeDecoration("chat.type.text.narrate"),
) : Generator {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

fun DataPack.chatType(
	fileName: String = "chat_type",
	block: ChatType.() -> Unit = {},
): ChatTypeArgument {
	chatTypes += ChatType(fileName).apply(block)
	return ChatTypeArgument(fileName, name)
}

fun ChatType.chat(translationKey: String = "chat.type.text", block: ChatTypeDecoration.() -> Unit = {}) {
	chat = ChatTypeDecoration(translationKey).apply(block)
}

fun ChatType.narration(translationKey: String = "chat.type.text.narrate", block: ChatTypeDecoration.() -> Unit = {}) {
	narration = ChatTypeDecoration(translationKey).apply(block)
}
