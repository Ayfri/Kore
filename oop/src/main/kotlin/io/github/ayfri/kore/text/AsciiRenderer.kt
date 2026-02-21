package io.github.ayfri.kore.text

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB

/** Configuration for the ANSI text → text component renderer. */
class AnsiRendererConfig {
	var color: Color? = null
	var bold: Boolean? = null
	var italic: Boolean? = null
	var font: String? = null
}

private data class AnsiStyle(
	var color: Color? = null,
	var bold: Boolean? = null,
	var italic: Boolean? = null,
	var underlined: Boolean? = null,
	var strikethrough: Boolean? = null,
	var obfuscated: Boolean? = null,
) {
	fun reset() {
		color = null
		bold = null
		italic = null
		underlined = null
		strikethrough = null
		obfuscated = null
	}

	fun copy() = AnsiStyle(color, bold, italic, underlined, strikethrough, obfuscated)
}

private val ANSI_STANDARD_COLORS = mapOf(
	30 to Color.BLACK,
	31 to Color.RED,
	32 to Color.GREEN,
	33 to Color.GOLD,
	34 to Color.BLUE,
	35 to Color.LIGHT_PURPLE,
	36 to Color.AQUA,
	37 to Color.WHITE,
)

private val ANSI_BRIGHT_COLORS = mapOf(
	90 to Color.DARK_GRAY,
	91 to Color.RED,
	92 to Color.GREEN,
	93 to Color.YELLOW,
	94 to Color.BLUE,
	95 to Color.LIGHT_PURPLE,
	96 to Color.AQUA,
	97 to Color.WHITE,
)

private fun ansi256ToColor(index: Int): Color = when (index) {
	in 0..7 -> ANSI_STANDARD_COLORS[index + 30] ?: Color.WHITE
	in 8..15 -> ANSI_BRIGHT_COLORS[index + 82] ?: Color.WHITE
	in 16..231 -> {
		val adjusted = index - 16
		val r = (adjusted / 36) * 51
		val g = ((adjusted % 36) / 6) * 51
		val b = (adjusted % 6) * 51
		RGB(r, g, b)
	}

	in 232..255 -> {
		val gray = (index - 232) * 10 + 8
		RGB(gray, gray, gray)
	}

	else -> Color.WHITE
}

private val ANSI_ESCAPE_REGEX = Regex("""\x1B\[([0-9;]*)m""")

private fun applySgrCodes(params: List<Int>, style: AnsiStyle) {
	var i = 0
	while (i < params.size) {
		when (val code = params[i]) {
			0 -> style.reset()
			1 -> style.bold = true
			3 -> style.italic = true
			4 -> style.underlined = true
			8 -> style.obfuscated = true
			9 -> style.strikethrough = true
			22 -> style.bold = null
			23 -> style.italic = null
			24 -> style.underlined = null
			28 -> style.obfuscated = null
			29 -> style.strikethrough = null
			39 -> style.color = null
			38 -> {
				if (i + 1 < params.size) {
					when (params[i + 1]) {
						5 -> {
							if (i + 2 < params.size) {
								style.color = ansi256ToColor(params[i + 2])
								i += 2
							}
						}

						2 -> {
							if (i + 4 < params.size) {
								style.color = RGB(
									params[i + 2].coerceIn(0, 255),
									params[i + 3].coerceIn(0, 255),
									params[i + 4].coerceIn(0, 255),
								)
								i += 4
							}
						}
					}
				}
			}

			in 30..37 -> style.color = ANSI_STANDARD_COLORS[code]
			in 90..97 -> style.color = ANSI_BRIGHT_COLORS[code]
			else -> {}
		}
		i++
	}
}

private data class StyledChar(
	val char: Char,
	val style: AnsiStyle,
)

private fun parseLine(line: String): List<StyledChar> {
	val result = mutableListOf<StyledChar>()
	val style = AnsiStyle()
	var pos = 0

	for (match in ANSI_ESCAPE_REGEX.findAll(line)) {
		for (i in pos until match.range.first) {
			result += StyledChar(line[i], style.copy())
		}
		val paramStr = match.groupValues[1]
		val params = if (paramStr.isEmpty()) listOf(0) else paramStr.split(';').mapNotNull { it.toIntOrNull() }
		applySgrCodes(params, style)
		pos = match.range.last + 1
	}

	for (i in pos until line.length) {
		result += StyledChar(line[i], style.copy())
	}

	return result
}

/**
 * Converts text containing ANSI SGR escape sequences into a list of [ChatComponents] (one per line).
 *
 * ANSI escape codes are stripped from the output and mapped to Minecraft text component styles
 * (bold, italic, underlined, strikethrough, obfuscated, color).
 * Consecutive characters sharing the same style are merged into runs for efficiency.
 */
fun ansiToTextComponents(text: String, block: AnsiRendererConfig.() -> Unit = {}): List<ChatComponents> {
	val cfg = AnsiRendererConfig().apply(block)
	return text.lines().map { line -> lineToComponents(line, cfg) }
}

private fun lineToComponents(line: String, cfg: AnsiRendererConfig): ChatComponents {
	val parsed = parseLine(line)
	if (parsed.isEmpty()) return textComponent()

	val components = mutableListOf<PlainTextComponent>()
	var currentStyle = parsed[0].style
	val buffer = StringBuilder()
	buffer.append(parsed[0].char)

	for (i in 1 until parsed.size) {
		val sc = parsed[i]
		if (sc.style == currentStyle) {
			buffer.append(sc.char)
		} else {
			components += buildComponent(buffer.toString(), cfg, currentStyle)
			buffer.clear()
			buffer.append(sc.char)
			currentStyle = sc.style
		}
	}
	components += buildComponent(buffer.toString(), cfg, currentStyle)

	val first = components.removeFirst()
	return if (components.isEmpty()) {
		ChatComponents(first)
	} else {
		val result = ChatComponents(first)
		result.list += components
		result
	}
}

private fun buildComponent(
	run: String,
	cfg: AnsiRendererConfig,
	ansi: AnsiStyle,
): PlainTextComponent = text(run) {
	color = ansi.color ?: cfg.color
	bold = ansi.bold ?: cfg.bold
	italic = ansi.italic ?: cfg.italic
	underlined = ansi.underlined
	strikethrough = ansi.strikethrough
	obfuscated = ansi.obfuscated
	font = cfg.font
}
