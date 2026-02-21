package io.github.ayfri.kore.text

import io.github.ayfri.kore.arguments.actions.OpenUrl
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB

/** Configuration for the Markdown → text component renderer. */
class MarkdownRendererConfig {
	var color: Color? = null
	var bold: Boolean? = null
	var italic: Boolean? = null
	var font: String? = null
	var headingColors: Map<Int, Color> = mapOf(
		1 to Color.GOLD,
		2 to Color.YELLOW,
		3 to Color.AQUA,
		4 to Color.GREEN,
		5 to Color.LIGHT_PURPLE,
		6 to Color.GRAY,
	)
	var codeColor: Color? = Color.GRAY
	var linkColor: Color? = Color.AQUA
	var linkUnderline: Boolean = true
	var blockquotePrefix: String = "│ "
	var bulletChar: String = "• "
	var hrChar: String = "─"
	var hrLength: Int = 20
}

private data class MdStyle(
	var bold: Boolean = false,
	var italic: Boolean = false,
	var strikethrough: Boolean = false,
	var underlined: Boolean = false,
	var obfuscated: Boolean = false,
	var color: Color? = null,
	var clickUrl: String? = null,
)

private sealed interface MdToken
private data class TextToken(val text: String) : MdToken
private data object BoldToggle : MdToken
private data object ItalicToggle : MdToken
private data object StrikethroughToggle : MdToken
private data object UnderlineToggle : MdToken
private data object ObfuscatedToggle : MdToken
private data class CodeSpan(val code: String) : MdToken
private data class LinkToken(val label: String, val url: String) : MdToken
private data class ColorStart(val color: Color) : MdToken
private data object ColorEnd : MdToken

private val INLINE_PATTERN = Regex(
	"""(?x)
	  (``.+?``)                             # backtick code (double)
	| (`.+?`)                               # backtick code (single)
	| (\*\*|__)                             # bold toggle (**) or underline (__)
	| (~~)                                  # strikethrough toggle
	| (\|\|)                                # obfuscated toggle
	| (\*|(?<!\w)_(?=\S))                   # italic toggle
	| (\[([^\]]+)]\(([^)]+)\))              # link [label](url)
	| (§\(([^)]*)\))                        # color §(#rrggbb) or §() to reset
	"""
)

private fun tokenize(input: String): List<MdToken> {
	val tokens = mutableListOf<MdToken>()
	var lastEnd = 0

	for (match in INLINE_PATTERN.findAll(input)) {
		if (match.range.first > lastEnd) {
			tokens += TextToken(input.substring(lastEnd, match.range.first))
		}
		val full = match.value

		when {
			match.groupValues[1].isNotEmpty() -> tokens += CodeSpan(match.groupValues[1].removeSurrounding("``"))
			match.groupValues[2].isNotEmpty() -> tokens += CodeSpan(match.groupValues[2].removeSurrounding("`"))
			match.groupValues[3].isNotEmpty() -> {
				if (match.groupValues[3] == "**") tokens += BoldToggle
				else tokens += UnderlineToggle
			}

			match.groupValues[4].isNotEmpty() -> tokens += StrikethroughToggle
			match.groupValues[5].isNotEmpty() -> tokens += ObfuscatedToggle
			match.groupValues[6].isNotEmpty() -> tokens += ItalicToggle
			match.groupValues[7].isNotEmpty() -> tokens += LinkToken(match.groupValues[8], match.groupValues[9])
			match.groupValues[10].isNotEmpty() -> {
				val colorStr = match.groupValues[11]
				if (colorStr.isEmpty()) {
					tokens += ColorEnd
				} else {
					try {
						tokens += ColorStart(RGB.fromHex(colorStr.removePrefix("#")))
					} catch (_: Exception) {
						tokens += TextToken(full)
					}
				}
			}

			else -> tokens += TextToken(full)
		}

		lastEnd = match.range.last + 1
	}

	if (lastEnd < input.length) {
		tokens += TextToken(input.substring(lastEnd))
	}

	return tokens
}

private fun renderInline(
	tokens: List<MdToken>,
	cfg: MarkdownRendererConfig,
	baseColor: Color? = null,
): List<PlainTextComponent> {
	val components = mutableListOf<PlainTextComponent>()
	val style = MdStyle()
	val colorStack = mutableListOf<Color?>()

	fun flush(txt: String) {
		if (txt.isEmpty()) return
		components += text(txt) {
			color = style.color ?: baseColor ?: cfg.color
			bold = if (style.bold) true else cfg.bold
			italic = if (style.italic) true else null
			strikethrough = if (style.strikethrough) true else null
			underlined = if (style.underlined) true else null
			obfuscated = if (style.obfuscated) true else null
			font = cfg.font
			style.clickUrl?.let { clickEvent = OpenUrl(it) }
		}
	}

	for (token in tokens) {
		when (token) {
			is TextToken -> flush(token.text)
			is BoldToggle -> style.bold = !style.bold
			is ItalicToggle -> style.italic = !style.italic
			is StrikethroughToggle -> style.strikethrough = !style.strikethrough
			is UnderlineToggle -> style.underlined = !style.underlined
			is ObfuscatedToggle -> style.obfuscated = !style.obfuscated

			is CodeSpan -> {
				components += text(token.code) {
					color = cfg.codeColor ?: cfg.color
					font = cfg.font
				}
			}

			is LinkToken -> {
				components += text(token.label) {
					color = cfg.linkColor ?: cfg.color
					underlined = if (cfg.linkUnderline) true else null
					clickEvent = OpenUrl(token.url)
					font = cfg.font
				}
			}

			is ColorStart -> {
				colorStack += style.color
				style.color = token.color
			}

			is ColorEnd -> {
				style.color = if (colorStack.isNotEmpty()) colorStack.removeLast() else null
			}
		}
	}

	return components
}

private val HEADING_REGEX = Regex("""^(#{1,6})\s+(.+)$""")
private val UNORDERED_LIST_REGEX = Regex("""^(\s*)[-*+]\s+(.+)$""")
private val ORDERED_LIST_REGEX = Regex("""^(\s*)\d+\.\s+(.+)$""")
private val BLOCKQUOTE_REGEX = Regex("""^>\s?(.*)$""")
private val HR_REGEX = Regex("""^(\*{3,}|-{3,}|_{3,})\s*$""")

/**
 * Converts a Markdown string into a list of [ChatComponents] (one per line).
 *
 * Supports headings, bold, italic, strikethrough, underline, obfuscated,
 * inline code, links, ordered/unordered lists, blockquotes, horizontal rules,
 * and custom `§(#rrggbb)` color spans.
 */
fun markdownToTextComponents(markdown: String, block: MarkdownRendererConfig.() -> Unit = {}): List<ChatComponents> {
	val cfg = MarkdownRendererConfig().apply(block)
	return markdown.lines().map { line -> renderLine(line, cfg) }
}

private fun renderLine(line: String, cfg: MarkdownRendererConfig): ChatComponents {
	if (HR_REGEX.matches(line)) {
		return ChatComponents(text(cfg.hrChar.repeat(cfg.hrLength)) {
			color = cfg.color ?: Color.GRAY
			strikethrough = true
			font = cfg.font
		})
	}

	HEADING_REGEX.matchEntire(line)?.let { match ->
		val level = match.groupValues[1].length
		val content = match.groupValues[2]
		val headingColor = cfg.headingColors[level] ?: cfg.color
		val tokens = tokenize(content)
		val components = renderInline(tokens, cfg, headingColor)
		components.forEach { it.bold = true }
		return componentsToChat(components)
	}

	BLOCKQUOTE_REGEX.matchEntire(line)?.let { match ->
		val content = match.groupValues[1]
		val prefixComp = text(cfg.blockquotePrefix) {
			color = Color.GRAY
			font = cfg.font
		}
		val tokens = tokenize(content)
		val inlineComps = renderInline(tokens, cfg)
		return componentsToChat(listOf(prefixComp) + inlineComps)
	}

	UNORDERED_LIST_REGEX.matchEntire(line)?.let { match ->
		val indent = match.groupValues[1]
		val content = match.groupValues[2]
		val bulletComp = text(indent + cfg.bulletChar) {
			color = cfg.color
			font = cfg.font
		}
		val tokens = tokenize(content)
		val inlineComps = renderInline(tokens, cfg)
		return componentsToChat(listOf(bulletComp) + inlineComps)
	}

	ORDERED_LIST_REGEX.matchEntire(line)?.let { match ->
		val indent = match.groupValues[1]
		val numStr = line.trimStart().substringBefore('.')
		val content = match.groupValues[2]
		val numComp = text("$indent$numStr. ") {
			color = cfg.color
			font = cfg.font
		}
		val tokens = tokenize(content)
		val inlineComps = renderInline(tokens, cfg)
		return componentsToChat(listOf(numComp) + inlineComps)
	}

	val tokens = tokenize(line)
	val components = renderInline(tokens, cfg)
	if (components.isEmpty()) return textComponent()
	return componentsToChat(components)
}

private fun componentsToChat(components: List<PlainTextComponent>): ChatComponents {
	if (components.isEmpty()) return textComponent()
	val first = components.first()
	return if (components.size == 1) {
		ChatComponents(first)
	} else {
		val result = ChatComponents(first)
		result.list += components.drop(1)
		result
	}
}
