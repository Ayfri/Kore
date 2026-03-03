package io.github.ayfri.kore.text

import io.github.ayfri.kore.arguments.actions.*
import io.github.ayfri.kore.arguments.chatcomponents.*
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverAction
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverEvent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.utils.nbt

/** Configuration for the MiniMessage → text component renderer. */
class MiniMessageRendererConfig {
    var color: Color? = null
    var bold: Boolean? = null
    var italic: Boolean? = null
    var font: String? = null
    var strict: Boolean = false
    var tagResolvers: Map<String, TagResolver> = emptyMap()
}

/** A tag resolver that dynamically provides a [ChatComponent] for a custom placeholder tag. */
fun interface TagResolver {
    fun resolve(): ChatComponent
}

/** Exception thrown in strict mode when a MiniMessage string contains parsing errors. */
class MiniMessageParseException(message: String) : RuntimeException(message)

private val NAMED_COLORS = buildMap {
    FormattingColor.entries.forEach { put(it.name.lowercase(), it as Color) }
}

private sealed interface MiniMessageToken
private data class MiniTextToken(val text: String) : MiniMessageToken
private data class OpenTag(val name: String, val args: List<String>) : MiniMessageToken
private data class CloseTag(val name: String) : MiniMessageToken

private fun tokenize(input: String): List<MiniMessageToken> {
    val tokens = mutableListOf<MiniMessageToken>()
    val buffer = StringBuilder()
    var i = 0

    while (i < input.length) {
        if (i < input.length - 1 && input[i] == '\\' && input[i + 1] == '<') {
            buffer.append('<')
            i += 2
            continue
        }

        if (input[i] == '<') {
            if (buffer.isNotEmpty()) {
                tokens += MiniTextToken(buffer.toString())
                buffer.clear()
            }

            val end = findTagEnd(input, i)
            if (end == -1) {
                buffer.append('<')
                i++
                continue
            }

            val tagContent = input.substring(i + 1, end)
            i = end + 1

            if (tagContent.startsWith("/")) {
                tokens += CloseTag(normalizeTagName(tagContent.substring(1).trim()))
            } else if (tagContent.startsWith("#") && tagContent.length == 7) {
                tokens += OpenTag("#", listOf(tagContent))
            } else {
                val parsed = parseTagContent(tagContent)
                tokens += OpenTag(normalizeTagName(parsed.first), parsed.second)
            }
        } else {
            buffer.append(input[i])
            i++
        }
    }

    if (buffer.isNotEmpty()) {
        tokens += MiniTextToken(buffer.toString())
    }

    return tokens
}

private fun findTagEnd(input: String, start: Int): Int {
    var i = start + 1
    var inQuote = false
    var quoteChar = ' '

    while (i < input.length) {
        val c = input[i]
        if (inQuote) {
            if (c == quoteChar) inQuote = false
        } else {
            when (c) {
                '\'' -> {
                    inQuote = true; quoteChar = '\''
                }

                '"' -> {
                    inQuote = true; quoteChar = '"'
                }

                '>' -> return i
            }
        }
        i++
    }
    return -1
}

private fun parseTagContent(content: String): Pair<String, List<String>> {
    val parts = mutableListOf<String>()
    val buffer = StringBuilder()
    var inQuote = false
    var quoteChar = ' '

    for (c in content) {
        if (inQuote) {
            if (c == quoteChar) {
                inQuote = false
            } else {
                buffer.append(c)
            }
        } else {
            when (c) {
                ':' -> {
                    parts += buffer.toString()
                    buffer.clear()
                }

                '\'', '"' -> {
                    inQuote = true
                    quoteChar = c
                }

                else -> buffer.append(c)
            }
        }
    }
    if (buffer.isNotEmpty()) parts += buffer.toString()

    val name = parts.removeFirstOrNull() ?: ""
    return name to parts
}

private fun normalizeTagName(name: String): String = name.lowercase().trim()

private data class StyleState(
    var color: Color? = null,
    var bold: Boolean? = null,
    var italic: Boolean? = null,
    var underlined: Boolean? = null,
    var strikethrough: Boolean? = null,
    var obfuscated: Boolean? = null,
    var font: String? = null,
    var insertion: String? = null,
    var clickEvent: ClickEvent? = null,
    var hoverEvent: HoverEvent? = null,
) {
    fun copy() =
        StyleState(color, bold, italic, underlined, strikethrough, obfuscated, font, insertion, clickEvent, hoverEvent)
}

private sealed interface TagEntry {
    val name: String
}

private data class StyleTagEntry(
    override val name: String,
    val previousState: StyleState,
) : TagEntry

/**
 * Converts a MiniMessage string into a [ChatComponents] tree.
 *
 * Supports colors (named, hex, gradients, rainbow, transition), decorations (bold, italic,
 * underlined, strikethrough, obfuscated), click events, hover events, translatable components,
 * keybind components, selector components, score components, NBT components, fonts, insertion,
 * newlines, reset, escaping, preformatted blocks, and custom tag resolvers.
 */
fun miniMessageToTextComponents(input: String, block: MiniMessageRendererConfig.() -> Unit = {}): ChatComponents {
    val cfg = MiniMessageRendererConfig().apply(block)
    val tokens = tokenize(input)
    return renderTokens(tokens, cfg)
}

private fun renderTokens(tokens: List<MiniMessageToken>, cfg: MiniMessageRendererConfig): ChatComponents {
    val components = mutableListOf<ChatComponent>()
    val style = StyleState(
        color = cfg.color,
        bold = cfg.bold,
        italic = cfg.italic,
        font = cfg.font,
    )
    val tagStack = mutableListOf<TagEntry>()
    var inPre = false
    val preBuffer = StringBuilder()

    fun flushText(txt: String) {
        if (txt.isEmpty()) return
        components += text(txt) {
            color = style.color
            bold = style.bold
            italic = style.italic
            underlined = style.underlined
            strikethrough = style.strikethrough
            obfuscated = style.obfuscated
            font = style.font
            insertion = style.insertion
            clickEvent = style.clickEvent
            hoverEvent = style.hoverEvent
        }
    }

    for (token in tokens) {
        if (inPre) {
            when (token) {
                is CloseTag -> {
                    if (token.name == "pre") {
                        flushText(preBuffer.toString())
                        preBuffer.clear()
                        inPre = false
                        val idx = tagStack.indexOfLast { it.name == "pre" }
                        if (idx >= 0) {
                            val entry = tagStack.removeAt(idx)
                            if (entry is StyleTagEntry) restoreStyle(style, entry.previousState)
                        }
                    } else {
                        preBuffer.append("</${token.name}>")
                    }
                }

                is OpenTag -> {
                    if (token.args.isEmpty()) {
                        preBuffer.append("<${token.name}>")
                    } else {
                        preBuffer.append("<${token.name}:${token.args.joinToString(":")}>")
                    }
                }

                is MiniTextToken -> preBuffer.append(token.text)
            }
            continue
        }

        when (token) {
            is MiniTextToken -> flushText(token.text)

            is OpenTag -> {
                val tagName = token.name
                val args = token.args

                when {
                    tagName == "#" && args.size == 1 -> {
                        pushStyle(tagStack, style, "#")
                        style.color = parseColor(args[0])
                    }

                    tagName == "color" || tagName == "c" || tagName == "colour" -> {
                        if (args.isNotEmpty()) {
                            pushStyle(tagStack, style, "color")
                            style.color = parseColor(args[0])
                        }
                    }

                    tagName in NAMED_COLORS -> {
                        pushStyle(tagStack, style, tagName)
                        style.color = NAMED_COLORS[tagName]
                    }

                    tagName == "bold" || tagName == "b" -> {
                        pushStyle(tagStack, style, "bold")
                        style.bold = true
                    }

                    tagName == "italic" || tagName == "i" || tagName == "em" -> {
                        pushStyle(tagStack, style, "italic")
                        style.italic = true
                    }

                    tagName == "underlined" || tagName == "u" -> {
                        pushStyle(tagStack, style, "underlined")
                        style.underlined = true
                    }

                    tagName == "strikethrough" || tagName == "st" -> {
                        pushStyle(tagStack, style, "strikethrough")
                        style.strikethrough = true
                    }

                    tagName == "obfuscated" || tagName == "obf" -> {
                        pushStyle(tagStack, style, "obfuscated")
                        style.obfuscated = true
                    }

                    tagName == "reset" || tagName == "r" -> {
                        pushStyle(tagStack, style, "reset")
                        style.color = cfg.color
                        style.bold = cfg.bold
                        style.italic = cfg.italic
                        style.underlined = null
                        style.strikethrough = null
                        style.obfuscated = null
                        style.font = cfg.font
                        style.insertion = null
                        style.clickEvent = null
                        style.hoverEvent = null
                    }

                    tagName == "click" -> {
                        if (args.size >= 2) {
                            pushStyle(tagStack, style, "click")
                            style.clickEvent = parseClickEvent(args[0], args.drop(1).joinToString(":"))
                        }
                    }

                    tagName == "hover" -> {
                        if (args.size >= 2) {
                            pushStyle(tagStack, style, "hover")
                            style.hoverEvent = parseHoverEvent(args, cfg)
                        }
                    }

                    tagName == "font" -> {
                        if (args.isNotEmpty()) {
                            pushStyle(tagStack, style, "font")
                            style.font = args.joinToString(":")
                        }
                    }

                    tagName == "insert" || tagName == "insertion" -> {
                        if (args.isNotEmpty()) {
                            pushStyle(tagStack, style, "insertion")
                            style.insertion = args.joinToString(":")
                        }
                    }

                    tagName == "newline" || tagName == "br" -> {
                        flushText("\n")
                    }

                    tagName == "lang" || tagName == "translate" || tagName == "tr" -> {
                        if (args.isNotEmpty()) {
                            val key = args[0]
                            val withArgs = args.drop(1)
                            val fallback = withArgs.firstOrNull { it.startsWith("\"") }?.removeSurrounding("\"")
                            val translationArgs = if (fallback != null) withArgs.drop(1) else withArgs
                            val withComponents = translationArgs.map { arg ->
                                val parsed = miniMessageToTextComponents(arg) { copyFrom(cfg) }
                                if (parsed.list.size == 1) parsed.list[0] else text(arg)
                            }.takeIf { it.isNotEmpty() }

                            components += TranslatedTextComponent(
                                translate = key,
                                with = withComponents,
                                fallback = fallback,
                            ).apply {
                                color = style.color
                                bold = style.bold
                                italic = style.italic
                                this.font = style.font
                                clickEvent = style.clickEvent
                                hoverEvent = style.hoverEvent
                            }
                        }
                    }

                    tagName == "key" || tagName == "keybind" -> {
                        if (args.isNotEmpty()) {
                            components += KeybindComponent(keybind = args[0]).apply {
                                color = style.color
                                bold = style.bold
                                italic = style.italic
                                this.font = style.font
                                clickEvent = style.clickEvent
                                hoverEvent = style.hoverEvent
                            }
                        }
                    }

                    tagName == "selector" || tagName == "sel" -> {
                        if (args.isNotEmpty()) {
                            components += EntityComponent(selector = args[0]).apply {
                                color = style.color
                                bold = style.bold
                                italic = style.italic
                                this.font = style.font
                                clickEvent = style.clickEvent
                                hoverEvent = style.hoverEvent
                            }
                        }
                    }

                    tagName == "score" -> {
                        if (args.size >= 2) {
                            components += ScoreComponent(
                                score = ScoreComponentEntry(entity = args[0], objective = args[1])
                            ).apply {
                                color = style.color
                                bold = style.bold
                                italic = style.italic
                                this.font = style.font
                                clickEvent = style.clickEvent
                                hoverEvent = style.hoverEvent
                            }
                        }
                    }

                    tagName == "nbt" -> {
                        if (args.size >= 3) {
                            val source = args[0]
                            val target = args[1]
                            val path = args[2]
                            val nbtComp = NbtComponent(nbt = path).apply {
                                when (source.lowercase()) {
                                    "block" -> {
                                        block = target
                                        this.source = NbtComponentSource.BLOCK
                                    }

                                    "entity" -> {
                                        entity = target
                                        this.source = NbtComponentSource.ENTITY
                                    }

                                    "storage" -> {
                                        storage = target
                                        this.source = NbtComponentSource.STORAGE
                                    }
                                }
                                color = style.color
                                bold = style.bold
                                italic = style.italic
                                this.font = style.font
                                clickEvent = style.clickEvent
                                hoverEvent = style.hoverEvent
                            }
                            components += nbtComp
                        }
                    }

                    tagName == "gradient" -> {
                        pushStyle(tagStack, style, "gradient")
                        if (args.isNotEmpty()) {
                            style.color = parseColor(args[0])
                        }
                    }

                    tagName == "rainbow" -> {
                        pushStyle(tagStack, style, "rainbow")
                    }

                    tagName == "transition" -> {
                        pushStyle(tagStack, style, "transition")
                        if (args.isNotEmpty()) {
                            style.color = parseColor(args[0])
                        }
                    }

                    tagName == "pre" -> {
                        pushStyle(tagStack, style, "pre")
                        inPre = true
                    }

                    else -> {
                        val resolver = cfg.tagResolvers[tagName]
                        if (resolver != null) {
                            val resolved = resolver.resolve()
                            resolved.color = resolved.color ?: style.color
                            resolved.bold = resolved.bold ?: style.bold
                            resolved.italic = resolved.italic ?: style.italic
                            resolved.font = resolved.font ?: style.font
                            components += resolved
                        } else if (cfg.strict) {
                            throw MiniMessageParseException("Unknown tag: <$tagName>")
                        }
                    }
                }
            }

            is CloseTag -> {
                val tagName = token.name
                val resolvedName = resolveClosingTagName(tagName)

                val idx = tagStack.indexOfLast { it.name == resolvedName }
                if (idx >= 0) {
                    val entry = tagStack.removeAt(idx)
                    if (entry is StyleTagEntry) restoreStyle(style, entry.previousState)
                } else if (cfg.strict) {
                    throw MiniMessageParseException("Closing tag </$tagName> has no matching opening tag.")
                }
            }
        }
    }

    if (cfg.strict && tagStack.isNotEmpty()) {
        val unclosed = tagStack.map { it.name }
        throw MiniMessageParseException("Unclosed tags: ${unclosed.joinToString(", ")}")
    }

    return when {
        components.isEmpty() -> textComponent()
        components.size == 1 -> ChatComponents(components[0])
        else -> {
            val result = ChatComponents(components[0])
            result.list += components.drop(1)
            result
        }
    }
}

private fun pushStyle(tagStack: MutableList<TagEntry>, style: StyleState, tagName: String) {
    tagStack += StyleTagEntry(tagName, style.copy())
}

private fun restoreStyle(style: StyleState, previous: StyleState) {
    style.color = previous.color
    style.bold = previous.bold
    style.italic = previous.italic
    style.underlined = previous.underlined
    style.strikethrough = previous.strikethrough
    style.obfuscated = previous.obfuscated
    style.font = previous.font
    style.insertion = previous.insertion
    style.clickEvent = previous.clickEvent
    style.hoverEvent = previous.hoverEvent
}

private fun resolveClosingTagName(name: String): String = when (name) {
    "b" -> "bold"
    "i", "em" -> "italic"
    "u" -> "underlined"
    "st" -> "strikethrough"
    "obf" -> "obfuscated"
    "r" -> "reset"
    "c", "colour" -> "color"
    "tr" -> "translate"
    "sel" -> "selector"
    "insertion" -> "insertion"
    else -> {
        if (name in NAMED_COLORS) name
        else name
    }
}

private fun parseColor(value: String): Color? {
    if (value.startsWith("#")) {
        return try {
            RGB.fromHex(value.removePrefix("#"))
        } catch (_: Exception) {
            null
        }
    }
    return NAMED_COLORS[value.lowercase()]
}

private fun parseClickEvent(action: String, value: String): ClickEvent? = when (action.lowercase()) {
    "open_url" -> OpenUrl(value)
    "run_command" -> RunCommand(value)
    "suggest_command" -> SuggestCommand(value)
    "change_page" -> ChangePage(value.toIntOrNull() ?: 1)
    "copy_to_clipboard" -> CopyToClipboard(value)
    else -> null
}

private fun parseHoverEvent(args: List<String>, cfg: MiniMessageRendererConfig): HoverEvent? {
    if (args.isEmpty()) return null
    val action = args[0].lowercase()

    return when (action) {
        "show_text" -> {
            val textContent = args.drop(1).joinToString(":")
            val parsed = miniMessageToTextComponents(textContent) { copyFrom(cfg) }
            HoverEvent(HoverAction.SHOW_TEXT, parsed.toNbtTag())
        }

        "show_item" -> {
            if (args.size >= 2) {
                val id = args[1]
                val count = args.getOrNull(2)?.toIntOrNull() ?: 1
                val contentsItem = io.github.ayfri.kore.arguments.chatcomponents.hover.ContentsItem(id, count)
                HoverEvent(HoverAction.SHOW_ITEM, "".nbt, contentsItem)
            } else null
        }

        "show_entity" -> {
            if (args.size >= 3) {
                val type = args[1]
                val uuid = args.getOrNull(2) ?: ""
                val name = args.getOrNull(3)
                val nameComponent = name?.let { text(it) }
                val contents =
                    io.github.ayfri.kore.arguments.chatcomponents.hover.ContentsEntityUUID(type, nameComponent, uuid)
                HoverEvent(HoverAction.SHOW_ENTITY, "".nbt, contents)
            } else null
        }

        else -> null
    }
}

private fun MiniMessageRendererConfig.copyFrom(other: MiniMessageRendererConfig) {
    color = other.color
    bold = other.bold
    italic = other.italic
    font = other.font
    strict = other.strict
    tagResolvers = other.tagResolvers
}
