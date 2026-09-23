package io.github.ayfri.kore.features.chattypes

import io.github.ayfri.kore.arguments.actions.ClickEvent
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverEvent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDoubleArrayRGBASerializer
import kotlinx.serialization.Serializable

@Serializable
data class ChatTypeStyle(
	var bold: Boolean? = null,
	var clickEvent: ClickEvent? = null,
	var color: Color? = null,
	var font: String? = null,
	var hoverEvent: HoverEvent? = null,
	var insertion: String? = null,
	var italic: Boolean? = null,
	var obfuscated: Boolean? = null,
	/** Written as a `[r, g, b, a]` array, the game reading `shadow_color` as an ARGB int or float array, never a color name. */
	@Serializable(ColorAsDoubleArrayRGBASerializer::class)
	var shadowColor: Color? = null,
	var strikethrough: Boolean? = null,
	var underlined: Boolean? = null,
)
