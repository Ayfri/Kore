package io.github.ayfri.kore.website

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import io.github.ayfri.kore.website.externals.MarkedToken
import io.github.ayfri.kore.website.externals.use
import io.github.ayfri.kore.website.pages.PageNotFound
import io.github.ayfri.kore.website.utils.jsObject
import org.jetbrains.compose.web.css.Style
import org.w3c.dom.parsing.DOMParser

private val imageAttributes = setOf("alt", "height", "src", "width")
private val imageTag = Regex("""^<img\s[^<>]*>$""", RegexOption.IGNORE_CASE)
private val lineBreakTag = Regex("""^<br\s*/?>$""", RegexOption.IGNORE_CASE)
private val safeUrlSchemes = setOf("http", "https", "mailto")
private val urlScheme = Regex("^([^:/?#]+):")

/** Browsers ignore whitespace and control characters inside a scheme, so `java\tscript:` must be caught as `javascript:`. */
private fun isSafeUrl(url: String) = urlScheme.find(url.filter { it > ' ' })?.groupValues[1]?.lowercase().let { it == null || it in safeUrlSchemes }

private fun neutralizeUnsafeHref(token: MarkedToken): Boolean {
	if (!isSafeUrl(token.href.orEmpty())) token.href = "#"
	return false
}

/** GitHub stores dropped-in release images as raw `<img>` tags, parsed in an inert document and stripped down to [imageAttributes]. */
private fun sanitizeImage(tag: String): String {
	val image = DOMParser().parseFromString(tag, "text/html").body?.firstElementChild ?: return ""
	image.getAttributeNames()
		.filter { it !in imageAttributes || it == "src" && !isSafeUrl(image.getAttribute(it).orEmpty()) }
		.forEach(image::removeAttribute)
	return image.outerHTML
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	// `marked` is a module-level singleton rendering changelogs straight into `innerHTML`, so raw HTML and script URLs are defused once here.
	remember {
		use(jsObject {
			renderer = jsObject {
				html = { token ->
					val html = token.text.trim()
					when {
						lineBreakTag.matches(html) -> html
						imageTag.matches(html) -> sanitizeImage(html)
						else -> token.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
					}
				}
				image = ::neutralizeUnsafeHref
				link = ::neutralizeUnsafeHref
			}
		})
	}

	KobwebApp {
		Style(GlobalStyle)
		content()
	}
}

@InitKobweb
fun initKobweb(context: InitKobwebContext) {
	context.router.setErrorPage {
		PageNotFound()
	}
}
