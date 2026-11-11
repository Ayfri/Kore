package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.ariaHidden
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHash
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.components.common.Callout
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.McSprite
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement

/** A doc page block as plain data, written by `DocMarkdownHandlers` in build-logic and drawn by [DocView]. */
sealed class DocNode

class DocCallout(val type: String, vararg val children: DocNode) : DocNode()

class DocCell(val tag: String, val align: String, vararg val children: DocNode) : DocNode()

class DocCode(val text: String) : DocNode()

class DocCodeBlock(val text: String, val lang: String?) : DocNode()

/** Covers `p b em ul ol li blockquote table thead tbody tr th td hr br`. */
class DocElement(val tag: String, vararg val children: DocNode) : DocNode()

class DocHeading(val level: Int, val id: String, val brandIcon: String?, vararg val children: DocNode) : DocNode()

class DocImage(val src: String, val alt: String) : DocNode()

class DocLink(val href: String, vararg val children: DocNode) : DocNode()

class DocSprite(val path: String, val label: String) : DocNode()

class DocText(val text: String) : DocNode()

@Composable
fun DocView(node: DocNode) {
	when (node) {
		is DocCallout -> Callout(node.type) { node.children.forEach { DocView(it) } }
		is DocCell -> TagElement<HTMLElement>(node.tag, { style { textAlign(node.align) } }) {
			node.children.forEach { DocView(it) }
		}

		is DocCode -> Code { Text(node.text) }
		/** The `language-null` class keeps a fence without a language out of `MarkdownLayoutStyle`'s `code:not([class*=language])` inline code style. */
		is DocCodeBlock -> CodeBlock(node.text, node.lang ?: "null")
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
	}
}
