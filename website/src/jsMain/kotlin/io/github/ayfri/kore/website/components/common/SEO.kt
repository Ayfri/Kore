package io.github.ayfri.kore.website.components.common

import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.utils.obj
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.asList

private const val DEFAULT_BASE_URL = "https://kore.ayfri.com"

private val baseUrl get() = AppGlobals["websiteUrl"] ?: DEFAULT_BASE_URL

private fun selectAllInHead(selector: String) = document.head!!.querySelectorAll(selector).asList()

private fun removeAllInHead(selector: String) = selectAllInHead(selector).forEach { (it as Element).remove() }

/** Creates the `<head>` element matching [selector] if it is missing, then applies [attributes] to it. */
private fun upsert(selector: String, tag: String, vararg attributes: Pair<String, String>): Element {
	val head = document.head!!
	val element = head.querySelector(selector) ?: document.createElement(tag).also(head::appendChild)
	attributes.forEach { (name, value) -> element.setAttribute(name, value) }
	return element
}

private fun metaName(name: String, content: String) = upsert("meta[name='$name']", "meta", "name" to name, "content" to content)

private fun metaProperty(property: String, content: String) =
	upsert("meta[property='$property']", "meta", "property" to property, "content" to content)

private fun link(rel: String, href: String) = upsert("link[rel='$rel']", "link", "rel" to rel, "href" to href)

fun setTitle(title: String) {
	document.title = title
	metaProperty("og:title", title)
	metaName("twitter:title", title)
}

fun setKeywords(vararg keywords: String) = metaName("keywords", keywords.joinToString(", "))

fun setDescription(description: String) {
	metaName("description", description)
	metaProperty("og:description", description)
	metaName("twitter:description", description)
}

fun setCanonical(url: String) {
	link("canonical", url)
	metaProperty("og:url", url)
	metaName("twitter:url", url)
}

fun setType(type: String) = metaProperty("og:type", type)

fun setTwitterCard(card: String) = metaName("twitter:card", card)

fun setTwitterCreator(creator: String) {
	metaName("twitter:creator", creator)
	metaName("twitter:site", creator)
}

fun setImage(url: String) {
	metaProperty("og:image", url)
	metaName("twitter:image", url)
}

fun setHrefLang(path: String) {
	listOf("en", "x-default").forEach { hreflang ->
		upsert(
			"link[hreflang='$hreflang']",
			"link",
			"rel" to "alternate",
			"hreflang" to hreflang,
			"href" to "$baseUrl$path",
		)
	}
}

fun setDates(publishDate: String?, modifiedDate: String?) {
	setOptionalMeta("meta[property='article:published_time']", publishDate) { metaProperty("article:published_time", it) }
	setOptionalMeta("meta[name='date']", publishDate) { metaName("date", it) }
	setOptionalMeta("meta[property='article:modified_time']", modifiedDate) { metaProperty("article:modified_time", it) }
	setOptionalMeta("meta[name='last-modified']", modifiedDate) { metaName("last-modified", it) }
}

private inline fun setOptionalMeta(selector: String, value: String?, set: (String) -> Unit) =
	if (value == null) removeAllInHead(selector) else set(value)

fun setJsonLd(
	title: String,
	description: String,
	publishDate: String?,
	modifiedDate: String?,
	keywords: String,
	path: String,
	slugs: List<String>,
) {
	val breadcrumbItems = arrayOf(
		obj {
			`@type` = "ListItem"
			position = 1
			name = "Home"
			item = "$baseUrl/"
		},
		*slugs.mapIndexed { index, slug ->
			obj {
				`@type` = "ListItem"
				position = index + 2
				name = slug.replace("-", " ").replaceFirstChar { it.uppercase() }
				item = "$baseUrl/${slugs.take(index + 1).joinToString("/")}"
			}
		}.toTypedArray()
	)

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
			`@id` = "$baseUrl$path"
		}
		publisher = obj {
			`@type` = "Organization"
			name = "Kore"
			url = "https://github.com/Ayfri/Kore"
			logo = obj {
				`@type` = "ImageObject"
				url = "$baseUrl/logo.png"
			}
		}
		this["keywords"] = keywords
		breadcrumb = obj {
			`@type` = "BreadcrumbList"
			itemListElement = breadcrumbItems
		}
	}

	upsert("script[type='application/ld+json']", "script", "type" to "application/ld+json").textContent = JSON.stringify(jsonLd)
}
