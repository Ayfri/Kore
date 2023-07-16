package features.chattype

import arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class ChatTypeStyle(
	var color: Color? = null,
	var font: String? = null,
	var bold: Boolean? = null,
	var italic: Boolean? = null,
	var underlined: Boolean? = null,
	var strikethrough: Boolean? = null,
	var obfuscated: Boolean? = null,
	var insertion: String? = null,
)
