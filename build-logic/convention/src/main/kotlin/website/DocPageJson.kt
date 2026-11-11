package website

import com.varabyte.kobwebx.gradle.markdown.children
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallBlock
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCallExtension
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.handlers.NodeScope
import groovy.json.JsonOutput.toJson
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.*
import org.commonmark.parser.IncludeSourceSpans
import org.commonmark.parser.Parser
import org.gradle.api.GradleException

/** Ordered so "GitHub Actions" wins over "GitHub". */
private val brandIcons = listOf(
	"CurseForge" to "curseforge",
	"GitHub Actions" to "githubactions",
	"GitHub" to "github",
	"Gradle" to "gradle",
	"IntelliJ" to "intellijidea",
	"Modrinth" to "modrinth",
	"Node.js" to "nodedotjs",
)

private val headingIdSeparator = Regex("[^a-z0-9]+")
private val lineBreakTag = Regex("""<br\s*/?>""")

/** The page body comes from `/doc-data/<route>.json`, Kobweb only keeps the route, the front matter and the `MarkdownLayout { }` root. */
fun MarkdownHandlers.emptyDocBodies() {
	val empty: NodeScope.(Node) -> String = { "" }

	blockquote.set(empty)
	code.set(empty)
	heading.set(empty)
	hr.set(empty)
	html.set(empty)
	ol.set(empty)
	p.set(empty)
	table.set(empty)
	ul.set(empty)
}

/** Kobweb's default parser without the front matter extension, which isn't accessible from build-logic. */
internal fun docParser(): Parser = Parser.builder()
	.extensions(listOf(AutolinkExtension.create(), KobwebCallExtension.create('{' to '}', ::docParser), TablesExtension.create(), TaskListItemsExtension.create()))
	.includeSourceSpans(IncludeSourceSpans.BLOCKS_AND_INLINES)
	.build()

private fun Node.plainText(): String = when (this) {
	is Code -> literal
	is Emphasis, is Heading, is Link, is StrongEmphasis -> children().joinToString("") { it.plainText() }
	is Text -> literal
	else -> ""
}

/** Removes the `[!TYPE]` marker from a blockquote and returns `TYPE`, or null when the blockquote isn't a callout. */
private fun BlockQuote.takeCalloutType(): String? {
	val paragraph = firstChild as? Paragraph ?: return null
	val text = paragraph.firstChild as? Text ?: return null
	if (!text.literal.startsWith("[!")) return null

	val type = text.literal.substringAfter("[!").substringBefore("]").uppercase()
	val remaining = text.literal.substringAfter("]").trimStart()
	if (remaining.isNotEmpty()) {
		text.literal = remaining
		return type
	}

	text.unlink()
	if (paragraph.firstChild == null) paragraph.unlink()
	return type
}

/** Link reference definitions draw nothing, and the paragraph of a tight list item is spliced away, as Kobweb does. */
private fun Node.docChildren(): List<Node> = children().filterNot { it is LinkReferenceDefinition }.flatMap {
	if (it is Paragraph && (it.parent?.parent as? ListBlock)?.isTight == true) it.docChildren() else listOf(it)
}.toList()

/** Soft breaks between lines become spaces, as in Kobweb's renderer. */
private object SoftLineBreakConversionVisitor : AbstractVisitor() {
	override fun visit(softLineBreak: SoftLineBreak) {
		val space = Text(" ")
		space.sourceSpans = softLineBreak.sourceSpans
		softLineBreak.insertAfter(space)
		softLineBreak.unlink()
	}
}

/** Sibling texts merge into one, as in Kobweb's renderer: the callout marker detection reads the merged text. */
private object TextMergingVisitor : AbstractVisitor() {
	override fun visit(text: Text) {
		(text.previous as? Text)?.let { textPrev ->
			val merged = Text(textPrev.literal + text.literal)
			merged.sourceSpans = textPrev.sourceSpans + text.sourceSpans
			textPrev.insertBefore(merged)
			textPrev.unlink()
			text.unlink()
		}
	}
}

/** A link to a `/docs/...` route (the page's own for a `#anchor` link) found in the page at [filePath], [line] being null without a source span. */
internal data class DocLinkRef(val anchor: String?, val filePath: String, val line: Int?, val route: String)

internal data class DocHeadingRef(val id: String, val level: Int, val text: String)

/**
 * Writes a parsed doc page as the JSON array `DocContent` decodes in the site: a string is a text, `[tag, ...children]` an element, and the tags
 * starting with `!` are the other `DocNode`s (`!>` callout, `!a` link, `!c` code, `!d` aligned cell, `!h` heading, `!i` image, `!k` code block,
 * `!s` sprite, `!w` widget). A node the site can't draw fails the build, there is no fallback renderer.
 */
internal class DocPageJson(
	private val filePath: String,
	private val headings: MutableList<DocHeadingRef>,
	private val links: MutableList<DocLinkRef>,
	private val path: String,
	private val widgets: MutableSet<String>,
) {
	fun write(document: Node): String {
		document.accept(SoftLineBreakConversionVisitor)
		document.accept(TextMergingVisitor)
		return document.docChildren().joinToString(",", "[", "]", transform = ::node)
	}

	/** `["tag", args..., children...]`, [args] being JSON already. */
	private fun element(tag: String, parent: Node?, vararg args: String): String =
		(listOf(toJson(tag), *args) + parent?.docChildren().orEmpty().map(::node)).joinToString(",", "[", "]")

	private val Node.line: Int?
		get() = generateSequence(this) { it.parent }.firstNotNullOfOrNull { it.sourceSpans.firstOrNull() }?.lineIndex?.plus(1)

	private fun Node.unsupported(what: String? = this::class.simpleName): Nothing {
		throw GradleException("Doc pages don't support $what (line $line of $filePath)")
	}

	private fun node(node: Node): String = when (node) {
		is BlockQuote -> node.takeCalloutType()?.let { element("!>", node, toJson(it)) } ?: element("blockquote", node)
		is BulletList -> element("ul", node)
		is Code -> element("!c", null, toJson(node.literal))
		is Emphasis -> element("em", node)
		is FencedCodeBlock -> element("!k", null, toJson(node.info?.takeIf { it.isNotBlank() }), toJson(node.literal))
		is HardLineBreak -> element("br", node)
		is Heading -> {
			val baseId = node.children().joinToString("") {
				it.plainText().takeIf { text -> text.isNotBlank() }?.lowercase()?.replace(headingIdSeparator, "-").orEmpty()
			}.trim('-')
			/** A title repeated in the page gets `-1`, `-2`... appended, so the outline, the search and the links reach each heading. */
			val id = generateSequence(0, Int::inc).map { if (it == 0) baseId else "$baseId-$it" }.first { candidate -> headings.none { it.id == candidate } }
			val headingText = node.plainText()
			if (id.isNotBlank()) headings += DocHeadingRef(id, node.level, headingText)
			val icon = brandIcons.firstOrNull { (brand) -> brand in headingText }?.second
			element("!h", node, node.level.toString(), toJson(id), toJson(icon))
		}

		is HtmlInline -> if (lineBreakTag.matches(node.literal)) element("br", node) else node.unsupported("the inline HTML ${node.literal}")
		is Image -> {
			val alt = toJson(node.children().joinToString("") { it.plainText() })
			if (node.destination.startsWith("mc:")) element("!s", null, toJson(node.destination.removePrefix("mc:")), alt)
			else element("!i", null, toJson(node.destination), alt)
		}

		/** Drawn as a fence without a language, as in Kobweb's renderer. */
		is IndentedCodeBlock -> if (node.parent is Document) element("!k", null, "null", toJson(node.literal)) else node.unsupported()
		is KobwebCallBlock -> {
			if (node.firstChild !== node.lastChild) node.unsupported("a {{{ }}} block with content")
			val call = (node.firstChild as? KobwebCall ?: node.unsupported("an empty {{{ }}} block")).partiallyQualifiedName
			widgets += call
			element("!w", null, toJson(call))
		}

		is Link -> if (node.destination.substringBefore('#').endsWith(".md") && "://" !in node.destination) {
			node.unsupported("the relative link ${node.destination}, link its /docs route")
		} else {
			val route = node.destination.substringBefore('#').ifEmpty { path }
			if (route.startsWith("/docs/")) {
				links += DocLinkRef(
					anchor = node.destination.substringAfter('#', "").ifEmpty { null },
					filePath = filePath,
					line = node.line,
					route = route,
				)
			}
			element("!a", node, toJson(node.destination))
		}
		is ListItem -> element("li", node)
		is OrderedList -> element("ol", node)
		is Paragraph -> element("p", node)
		is StrongEmphasis -> element("b", node)
		is TableBlock -> element("table", node)
		is TableBody -> element("tbody", node)
		is TableCell -> {
			val cellTag = if (node.isHeader) "th" else "td"
			node.alignment?.let { element("!d", node, toJson(cellTag), toJson(it.name.lowercase())) } ?: element(cellTag, node)
		}

		is TableHead -> element("thead", node)
		is TableRow -> element("tr", node)
		is Text -> toJson(node.literal)
		is ThematicBreak -> element("hr", node)
		else -> node.unsupported()
	}
}
