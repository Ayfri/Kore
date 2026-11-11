package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.ariaHidden
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHash
import io.github.ayfri.kore.website.DocWidgetCall
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.components.common.Callout
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.McSprite
import kotlinx.browser.document
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.xhr.XMLHttpRequest

private const val DATA_TAG_ID = "doc-data"

/** A doc page block as plain data, decoded from `/doc-data/<route>.json` (written by `DocPageJson` in build-logic) and drawn by [DocView]. */
private sealed class DocNode

private class DocCallout(val type: String, val children: List<DocNode>) : DocNode()

private class DocCell(val tag: String, val align: String, val children: List<DocNode>) : DocNode()

private class DocCode(val text: String) : DocNode()

private class DocCodeBlock(val text: String, val lang: String?) : DocNode()

/** Covers `p b em ul ol li blockquote table thead tbody tr th td hr br`. */
private class DocElement(val tag: String, val children: List<DocNode>) : DocNode()

private class DocHeading(val level: Int, val id: String, val brandIcon: String?, val children: List<DocNode>) : DocNode()

private class DocImage(val src: String, val alt: String) : DocNode()

private class DocLink(val href: String, val children: List<DocNode>) : DocNode()

private class DocSprite(val path: String, val label: String) : DocNode()

private class DocText(val text: String) : DocNode()

private class DocWidget(val call: String) : DocNode()

private fun decode(raw: Any?): DocNode {
	if (raw is String) return DocText(raw)
	val parts = raw as Array<*>
	fun children(from: Int) = parts.drop(from).map(::decode)
	return when (val tag = parts[0] as String) {
		"!>" -> DocCallout(parts[1] as String, children(2))
		"!a" -> DocLink(parts[1] as String, children(2))
		"!c" -> DocCode(parts[1] as String)
		"!d" -> DocCell(parts[1] as String, parts[2] as String, children(3))
		"!h" -> DocHeading(parts[1] as Int, parts[2] as String, parts[3] as String?, children(4))
		"!i" -> DocImage(parts[1] as String, parts[2] as String)
		"!k" -> DocCodeBlock(parts[2] as String, parts[1] as String?)
		"!s" -> DocSprite(parts[1] as String, parts[2] as String)
		"!w" -> DocWidget(parts[1] as String)
		else -> DocElement(tag, children(1))
	}
}

/**
 * Reads the JSON synchronously, so the first render never waits for data: Kobweb's export snapshots the DOM at `load` with no ready hook, and the app
 * clears the root without hydration. The exported inline tag is read first, a request only runs without it. A host answering a missing file with its
 * HTML fallback must not crash `JSON.parse`, hence the `[` check.
 */
private fun docNodes(route: String): List<DocNode> {
	val tag = document.getElementById(DATA_TAG_ID)
	val json = if (tag?.getAttribute("data-route") == route) tag.textContent.orEmpty() else {
		val url = "/doc-data$route.json"
		val request = XMLHttpRequest()
		request.open("GET", url, false)
		request.send()
		if (request.status.toInt() != 200 || !request.responseText.startsWith("[")) {
			console.error("No doc data at $url (HTTP ${request.status})")
			return listOf(DocCallout("WARNING", listOf(DocElement("p", listOf(DocText("No doc data at $url, rerun kobwebxMarkdownProcess."))))))
		}

		if (AppGlobals.isExporting) {
			document.body!!.append(document.createElement("script").apply {
				id = DATA_TAG_ID
				setAttribute("type", "application/json")
				setAttribute("data-route", route)
				textContent = request.responseText.replace("<", "\\u003c")
			})
		}
		request.responseText
	}
	return JSON.parse<Array<Any?>>(json).map(::decode)
}

@Composable
fun DocContent(route: String) {
	remember(route) { docNodes(route) }.forEach { DocView(it) }
}

@Composable
private fun DocView(node: DocNode) {
	when (node) {
		is DocCallout -> Callout(node.type) { node.children.forEach { DocView(it) } }
		is DocCell -> TagElement<HTMLElement>(node.tag, { style { textAlign(node.align) } }) {
			node.children.forEach { DocView(it) }
		}

		is DocCode -> Code { Text(node.text) }
		/**
		 * The `language-none` class keeps a fence without a language out of `MarkdownLayoutStyle`'s `code:not([class*=language])` inline code
		 * style. `none` is the name Prism labels "Plain text", any other one shows up capitalized in the block header.
		 */
		is DocCodeBlock -> CodeBlock(node.text, node.lang ?: "none")
		is DocElement -> TagElement<HTMLElement>(node.tag, null) { node.children.forEach { DocView(it) } }
		is DocHeading -> TagElement<HTMLHeadingElement>("h${node.level}", {
			if (node.id.isNotBlank()) attr("id", node.id)
			if (node.level > 1) classes(MarkdownLayoutStyle.heading)
		}) {
			A("#${node.id}", { classes(MarkdownLayoutStyle.anchor) }) {
				LucideHash(modifier = Modifier.ariaHidden())
			}
			node.brandIcon?.let { BrandIcon(it) }
			node.children.forEach { DocView(it) }
		}

		is DocImage -> Img(node.src, node.alt) {
			attr("loading", "lazy")
			attr("decoding", "async")
		}

		is DocLink -> A(node.href) { node.children.forEach { DocView(it) } }
		is DocSprite -> McSprite(node.path, node.label)
		is DocText -> Text(node.text)
		is DocWidget -> DocWidgetCall(node.call)
	}
}
