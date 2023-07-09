package features.chattype

import DataPack
import Generator
import arguments.Argument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import java.io.File

@Serializable
data class ChatType(
	@Transient
	var fileName: String = "chat_type",
	var chat: ChatTypeDecoration = ChatTypeDecoration("chat.type.text"),
	var narration: ChatTypeDecoration = ChatTypeDecoration("chat.type.text.narrate"),
) : Generator {
	override fun generate(dataPack: DataPack, directory: File) {
		File(directory, "$fileName.json").writeText(dataPack.jsonEncoder.encodeToString(this))
	}
}

fun DataPack.chatType(
	fileName: String = "chat_type",
	block: ChatType.() -> Unit = {},
): Argument.ChatType {
	chatTypes += ChatType(fileName).apply(block)
	return Argument.ChatType(fileName, name)
}

fun ChatType.chat(translationKey: String = "chat.type.text", block: ChatTypeDecoration.() -> Unit = {}) {
	chat = ChatTypeDecoration(translationKey).apply(block)
}

fun ChatType.narration(translationKey: String = "chat.type.text.narrate", block: ChatTypeDecoration.() -> Unit = {}) {
	narration = ChatTypeDecoration(translationKey).apply(block)
}
