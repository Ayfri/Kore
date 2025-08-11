package io.github.ayfri.kore.features.chattypes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.types.ChatTypeArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven chat type defining how messages are formatted and narrated.
 *
 * A chat type specifies the translation keys and decorations for normal chat and for accessibility
 * narration. By declaring custom chat types you can control how messages appear in the chat HUD and
 * how they are narrated to players who enabled narration.
 *
 * Docs: https://kore.ayfri.com/docs/Chat_Components.html#chat-types
 * Minecraft Wiki: https://minecraft.wiki/w/Chat#Chat_types
 */
@Serializable
data class ChatType(
	@Transient
	override var fileName: String = "chat_type",
	var chat: ChatTypeDecoration = ChatTypeDecoration("chat.type.text"),
	var narration: ChatTypeDecoration = ChatTypeDecoration("chat.type.text.narrate"),
) : Generator("chat_type") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Register a new chat type file in the datapack.
 *
 * Produces `data/<namespace>/chat_type/<fileName>.json`. Use the builder `block` to tweak the
 * `chat` and `narration` decorations (translation keys, parameters, styles).
 */
fun DataPack.chatType(
	fileName: String = "chat_type",
	block: ChatType.() -> Unit = {},
): ChatTypeArgument {
	val chatType = ChatType(fileName).apply(block)
	chatTypes += chatType
	return ChatTypeArgument(fileName, chatType.namespace ?: name)
}

/** Configure the chat decoration for normal chat messages. */
fun ChatType.chat(translationKey: String = "chat.type.text", block: ChatTypeDecoration.() -> Unit = {}) {
	chat = ChatTypeDecoration(translationKey).apply(block)
}

/** Configure the narration decoration used by accessibility narration. */
fun ChatType.narration(translationKey: String = "chat.type.text.narrate", block: ChatTypeDecoration.() -> Unit = {}) {
	narration = ChatTypeDecoration(translationKey).apply(block)
}
