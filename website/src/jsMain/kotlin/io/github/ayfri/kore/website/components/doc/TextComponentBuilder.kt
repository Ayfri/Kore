package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.overflowX
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.mc.McChat
import io.github.ayfri.kore.website.components.mc.McChatLine
import io.github.ayfri.kore.website.components.mc.McColor
import io.github.ayfri.kore.website.components.mc.McSpan
import io.github.ayfri.kore.website.components.mc.chatColors
import io.github.ayfri.kore.website.utils.highlightCodeIn
import org.jetbrains.compose.web.dom.*

private const val DEFAULT_TEXT = "Welcome to the server!"
private const val KOTLIN_ID = "text-builder-kotlin"

private enum class ChatStyle { BOLD, ITALIC, STRIKETHROUGH, UNDERLINED }

/** Styles a chat line with a color and a few styles, drawn as the game does and written as the `tellraw` call of Kore. */
@Composable
fun TextComponentBuilder() {
	var color by remember { mutableStateOf<Pair<String, String>?>(null) }
	var styles by remember { mutableStateOf(emptySet<ChatStyle>()) }
	var text by remember { mutableStateOf(DEFAULT_TEXT) }

	val shown = text.ifBlank { DEFAULT_TEXT }
	val call = "textComponent(${listOfNotNull(shown.kotlin(), color?.let { "Color.${it.first}" }).joinToString(", ")})"
	val flags = ChatStyle.entries.filter { it in styles }
	val kotlin = if (flags.isEmpty()) "tellraw(allPlayers(), $call)"
	else flags.joinToString("\n\t", "tellraw(allPlayers(), $call {\n\t", "\n})") { "${it.name.lowercase()} = true" }

	Div({ classes(DocControlsStyle.widget) }) {
		Div({ classes(DocControlsStyle.panel, DocControlsStyle.stack) }) {
			TextField("text-builder-text", "text", text, DEFAULT_TEXT, 60) { text = it }
			ControlField("color") {
				PillGroup("Color", chatColors, color, { it.first }) { chosen -> color = chosen.takeUnless { it == color } }
			}
			ControlField("style") {
				ToggleGroup("Style", ChatStyle.entries, styles, { it.name.lowercase() }) { chosen ->
					styles = if (chosen in styles) styles - chosen else styles + chosen
				}
			}
		}

		Div({
			classes(DocControlsStyle.panel)
			style { overflowX(Overflow.Auto) }
		}) {
			McChat {
				McChatLine(
					McSpan(
						shown,
						color?.second ?: McColor.WHITE,
						bold = ChatStyle.BOLD in styles,
						italic = ChatStyle.ITALIC in styles,
						underlined = ChatStyle.UNDERLINED in styles,
						strikethrough = ChatStyle.STRIKETHROUGH in styles,
					)
				)
			}
		}

		Div({ id(KOTLIN_ID) }) { key(kotlin) { CodeBlock(kotlin, "kotlin") } }
		highlightCodeIn(KOTLIN_ID, kotlin)
	}
}
