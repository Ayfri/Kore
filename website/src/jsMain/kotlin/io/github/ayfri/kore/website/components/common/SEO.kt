package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.utils.Script
import io.github.ayfri.kore.website.utils.obj
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLTitleElement
import org.w3c.dom.asList

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
	Link("canonical", url)
	MetaProperty("og:url", url)
	MetaProperty("twitter:url", url)
}

fun setType(type: String) = renderComposable(document.head!!) {
	MetaProperty("og:type", type)
}

fun setTwitterCard(card: String) = renderComposable(document.head!!) {
	MetaName("twitter:card", card)
}

fun setTwitterCreator(creator: String) = renderComposable(document.head!!) {
	MetaName("twitter:creator", creator)
	MetaName("twitter:site", creator)
}

fun setImage(url: String) = renderComposable(document.head!!) {
	selectAll("meta[property*=image]").forEach(HTMLElement::remove)

	MetaProperty("og:image", url)
	MetaProperty("twitter:image", url)
}

fun setHrefLang(path: String) = renderComposable(document.head!!) {
	selectAll("meta[hreflang]").forEach(HTMLElement::remove)

	Meta {
		attr("rel", "alternate")
		attr("hreflang", "en")
		attr("href", "https://kore.ayfri.com$path")
	}
	Meta {
		attr("rel", "alternate")
		attr("hreflang", "x-default")
		attr("href", "https://kore.ayfri.com$path")
	}
}

fun setDates(publishDate: String?, modifiedDate: String?) = renderComposable(document.head!!) {
	selectAll("meta[property*=date], meta[name*=date]").forEach(HTMLElement::remove)

	publishDate?.let {
		MetaProperty("article:published_time", it)
		MetaName("date", it)
	}

	modifiedDate?.let {
		MetaProperty("article:modified_time", it)
		MetaName("last-modified", it)
	}
}

fun setJsonLd(
	title: String,
	description: String,
	publishDate: String?,
	modifiedDate: String?,
	keywords: String,
	path: String,
	slugs: List<String>,
) = renderComposable(document.head!!) {
	selectAll("script[type='application/ld+json']").forEach(HTMLElement::remove)

	val jsonLd = obj {
		`@context` = "https://schema.org"
		`@type` = "TechArticle"
		headline = title
		this["description"] = description
		author = obj {
			`@type` = "Organization"
			name = "Kore"
			url = "https://github.com/Ayfri/Kore"
		}
		datePublished = publishDate
		dateModified = modifiedDate
		mainEntityOfPage = obj {
			`@type` = "WebPage"
			`@id` = "https://kore.ayfri.com$path"
		}
		publisher = obj {
			`@type` = "Organization"
			name = "Kore"
			url = "https://github.com/Ayfri/Kore"
			logo = obj {
				`@type` = "ImageObject"
				url = "https://kore.ayfri.com/logo.png"
			}
		}
		this["keywords"] = keywords
		breadcrumb = obj {
			`@type` = "BreadcrumbList"
			itemListElement = slugs.mapIndexed { index, slug ->
				obj {
					`@type` = "ListItem"
					position = index + 2
					name = slug.replace("-", " ").replaceFirstChar { it.uppercase() }
					item = "https://kore.ayfri.com/${slugs.take(index + 1).joinToString("/")}"
				}
			}.toTypedArray().also {
				arrayOf(
					obj {
						`@type` = "ListItem"
						position = 1
						name = "Home"
						item = "https://kore.ayfri.com/"
					},
					*it
				)
			}
		}
	}

	Script(type = "application/ld+json") {
		Text(JSON.stringify(jsonLd))
	}
}
