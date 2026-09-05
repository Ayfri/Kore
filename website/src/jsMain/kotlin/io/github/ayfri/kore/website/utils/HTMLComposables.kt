package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement

private val searchBuilder = ElementBuilder.createBuilder<HTMLElement>("search")

@Composable
fun Search(
	attrs: AttrBuilderContext<HTMLElement>? = null,
	content: ContentBuilder<HTMLElement>? = null,
) {
	TagElement(
		elementBuilder = searchBuilder,
		applyAttrs = attrs,
		content = content
	)
}
