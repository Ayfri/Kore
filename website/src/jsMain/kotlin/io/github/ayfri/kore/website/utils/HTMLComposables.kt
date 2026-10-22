package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement

private val detailsBuilder = ElementBuilder.createBuilder<HTMLElement>("details")
private val searchBuilder = ElementBuilder.createBuilder<HTMLElement>("search")
private val summaryBuilder = ElementBuilder.createBuilder<HTMLElement>("summary")

/** A disclosure widget, closed unless it carries the `open` attribute. */
@Composable
fun Details(
	attrs: AttrBuilderContext<HTMLElement>? = null,
	content: ContentBuilder<HTMLElement>? = null,
) {
	TagElement(
		elementBuilder = detailsBuilder,
		applyAttrs = attrs,
		content = content
	)
}

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

/** The always visible label of a [Details]. */
@Composable
fun Summary(
	attrs: AttrBuilderContext<HTMLElement>? = null,
	content: ContentBuilder<HTMLElement>? = null,
) {
	TagElement(
		elementBuilder = summaryBuilder,
		applyAttrs = attrs,
		content = content
	)
}
