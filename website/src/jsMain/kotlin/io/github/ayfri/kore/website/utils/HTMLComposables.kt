package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLDetailsElement

@Composable
fun Details(
	attrs: AttrBuilderContext<HTMLDetailsElement>? = null,
	content: ContentBuilder<HTMLDetailsElement>? = null,
) {
	TagElement(
		elementBuilder = ElementBuilder.createBuilder("details"),
		applyAttrs = attrs,
		content = content
	)
}

@Composable
fun Summary(
	attrs: AttrBuilderContext<HTMLDetailsElement>? = null,
	content: ContentBuilder<HTMLDetailsElement>? = null,
) {
	TagElement(
		elementBuilder = ElementBuilder.createBuilder("summary"),
		applyAttrs = attrs,
		content = content
	)
}
