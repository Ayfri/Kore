package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.HTMLSpanElement

@Composable
fun A(href: String, content: String = "", vararg classes: String = arrayOf("link"), attrs: AttrsScope<HTMLAnchorElement>.() -> Unit = {}) {
	A(href, {
		classes(*classes)
		attrs()
	}) {
		Text(content)
	}
}

fun AttrsScope<HTMLAnchorElement>.rel(vararg values: String) {
    attr("rel", values.joinToString(" "))
}

@Composable
fun Img(attrs: AttrsScope<HTMLImageElement>.() -> Unit = {}) {
	TagElement<HTMLImageElement>(
		elementBuilder = ElementBuilder.createBuilder("img"),
		applyAttrs = {
			apply(attrs)
		},
		content = null
	)
}

@Composable
fun P(text: String, vararg classes: String = emptyArray(), attrs: AttrsScope<HTMLParagraphElement>.() -> Unit = {}) {
	P({
		classes(*classes)
		attrs()
	}) {
		Text(text)
	}
}

@Composable
fun Span(text: String, vararg classes: String = emptyArray(), attrs: AttrsScope<HTMLSpanElement>.() -> Unit = {}) {
	Span({
		classes(*classes)
		attrs()
	}) {
		Text(text)
	}
}
