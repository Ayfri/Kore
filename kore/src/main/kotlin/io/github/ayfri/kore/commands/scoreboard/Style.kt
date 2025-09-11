package io.github.ayfri.kore.commands.scoreboard

import io.github.ayfri.kore.arguments.actions.ClickEvent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverEvent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class Style(
	var bold: Boolean? = null,
	var clickEvent: ClickEvent? = null,
	var color: Color? = null,
	var extra: ChatComponents? = null,
	var font: String? = null,
	var hoverEvent: HoverEvent? = null,
	var insertion: String? = null,
	var italic: Boolean? = null,
	var keybind: String? = null,
	var obfuscated: Boolean? = null,
	var strikethrough: Boolean? = null,
	var underlined: Boolean? = null,
)
