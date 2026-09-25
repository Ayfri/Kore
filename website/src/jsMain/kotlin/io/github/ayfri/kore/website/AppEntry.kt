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

private val lineBreakTag = Regex("""^<br\s*/?>$""", RegexOption.IGNORE_CASE)
private val safeUrlSchemes = setOf("http", "https", "mailto")
private val urlScheme = Regex("^([^:/?#]+):")

/** Browsers ignore whitespace and control characters inside a scheme, so `java\tscript:` must be caught as `javascript:`. */
private fun neutralizeUnsafeHref(token: MarkedToken): Boolean {
	val scheme = urlScheme.find(token.href.orEmpty().filter { it > ' ' })?.groupValues[1]?.lowercase()
	if (scheme != null && scheme !in safeUrlSchemes) token.href = "#"
	return false
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
					if (lineBreakTag.matches(html)) html
					else token.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
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
