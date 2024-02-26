package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLTitleElement
import org.w3c.dom.asList
import kotlinx.browser.document

@Composable
fun Title(text: String) = TagElement<HTMLTitleElement>(
	elementBuilder = ElementBuilder.createBuilder("title"),
	applyAttrs = {},
	content = {
		Text(text)
	}
)

@Composable
fun Meta(attrs: AttrsScope<HTMLImageElement>.() -> Unit = {}) = TagElement<HTMLImageElement>(
	elementBuilder = ElementBuilder.createBuilder("meta"),
	applyAttrs = {
		apply(attrs)
	},
	content = null
)

@Composable
fun MetaProperty(property: String, content: String) = Meta {
	attr("property", property)
	attr("content", content)
}

@Composable
fun MetaName(name: String, content: String) = Meta {
	attr("name", name)
	attr("content", content)
}

@Composable
fun Link(rel: String, href: String) = TagElement<HTMLImageElement>(
	elementBuilder = ElementBuilder.createBuilder("link"),
	applyAttrs = {
		attr("rel", rel)
		attr("href", href)
	},
	content = null
)

fun selectAll(selector: String) = document.querySelectorAll(selector).asList().unsafeCast<List<HTMLElement>>()

fun setTitle(title: String) = renderComposable(document.head!!) {
	document.querySelector("title")?.remove()

	Title(title)
	MetaProperty("og:title", title)
	MetaProperty("twitter:title", title)
}

fun setDescription(description: String) = renderComposable(document.head!!) {
	selectAll("meta[property*=description]").forEach(HTMLElement::remove)
	document.querySelector("meta[name=description]")?.remove()

	MetaName("description", description)
	MetaProperty("og:description", description)
	MetaProperty("twitter:description", description)
}

fun setCanonical(url: String) = renderComposable(document.head!!) {
	val cleanedURL = url.replace(Regex("/+$"), "/")

	Link("canonical", cleanedURL)
	MetaProperty("og:url", cleanedURL)
	MetaProperty("twitter:url", cleanedURL)
}
