package io.github.ayfri.kore.features.chattype

import io.github.ayfri.kore.arguments.colors.Color
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
