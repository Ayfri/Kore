package io.github.ayfri.kore.website.components.common

import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.kore.website.GITHUB_LINK
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

/** Sets the social card to `/og/[card].png`, one of the 1200x630 PNGs rendered at build time by `OgImageRenderer`. */
fun setImage(card: String, alt: String) {
	val url = "$baseUrl/og/$card.png"
	metaProperty("og:image", url)
	metaProperty("og:image:alt", alt)
	metaProperty("og:image:height", "630")
	metaProperty("og:image:type", "image/png")
	metaProperty("og:image:width", "1200")
	metaName("twitter:image", url)
	metaName("twitter:image:alt", alt)
}

/** Site-wide metadata, the JSON-LD graph giving crawlers and AI answer engines the Kore entity, its repository and its author. */
fun setSiteMetadata() {
	metaProperty("og:locale", "en_US")
	metaProperty("og:site_name", "Kore")
	metaName("theme-color", "#24282e")

	val person = obj {
		`@type` = "Person"
		`@id` = "$baseUrl/#author"
		name = "Pierre Roy"
		alternateName = "Ayfri"
		url = "https://ayfri.com"
		sameAs = arrayOf("https://github.com/Ayfri")
	}
	val jsonLd = obj {
		`@context` = "https://schema.org"
		`@graph` = arrayOf(
			obj {
				`@type` = "WebSite"
				`@id` = "$baseUrl/#website"
				name = "Kore"
				url = "$baseUrl/"
				publisher = obj { `@id` = "$baseUrl/#author" }
			},
			obj {
				`@type` = "SoftwareSourceCode"
				`@id` = "$baseUrl/#software"
				name = "Kore"
				this["description"] = "Type-safe Kotlin DSL generating Minecraft Java Edition datapacks."
				codeRepository = GITHUB_LINK
				programmingLanguage = "Kotlin"
				license = "https://www.gnu.org/licenses/gpl-3.0.html"
				version = AppGlobals["projectVersion"]
				author = obj { `@id` = "$baseUrl/#author" }
			},
			person,
		)
	}

	upsert("script#site-json-ld", "script", "id" to "site-json-ld", "type" to "application/ld+json").textContent = JSON.stringify(jsonLd)
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
		author = obj { `@id` = "$baseUrl/#author" }
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

	upsert("script#page-json-ld", "script", "id" to "page-json-ld", "type" to "application/ld+json").textContent = JSON.stringify(jsonLd)
}
