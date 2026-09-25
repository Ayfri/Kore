package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.ariaHidden
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHash
import io.github.ayfri.kore.website.components.common.BrandIcon
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement

/** Anchored heading of the doc pages, one call per Markdown heading keeps them small, which speeds up KSP, linking and bundling. */
@Composable
fun MarkdownHeading(level: Int, id: String, brandIcon: String? = null, content: @Composable () -> Unit) {
	val attrs: AttrsScope<HTMLHeadingElement>.() -> Unit = {
		if (id.isNotBlank()) attr("id", id)
		if (level > 1) classes(MarkdownLayoutStyle.heading)
	}
	val body: @Composable () -> Unit = {
		A("#$id", { classes(MarkdownLayoutStyle.anchor) }) {
			LucideHash(modifier = Modifier.ariaHidden())
		}
		brandIcon?.let { BrandIcon(it) }
		content()
	}
	when (level) {
		1 -> H1(attrs) { body() }
		2 -> H2(attrs) { body() }
		3 -> H3(attrs) { body() }
		4 -> H4(attrs) { body() }
		5 -> H5(attrs) { body() }
		else -> H6(attrs) { body() }
	}
}

@Composable
fun InlineCode(text: String) = Code { Text(text) }

/** Markdown element whose only child is a text or an inline code, e.g. `MarkdownLeaf("td", "x", align = "center")` for `<td style="text-align: center">x</td>`. */
@Composable
fun MarkdownLeaf(tag: String, text: String, code: Boolean = false, href: String? = null, align: String? = null) {
	TagElement<HTMLElement>(tag, {
		href?.let { attr("href", it) }
		align?.let { style { property("text-align", it) } }
	}) {
		if (code) InlineCode(text) else Text(text)
	}
}
