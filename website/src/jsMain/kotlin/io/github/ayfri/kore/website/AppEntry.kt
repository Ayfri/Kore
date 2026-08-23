package io.github.ayfri.kore.website

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import io.github.ayfri.kore.website.externals.MarkedOptions
import io.github.ayfri.kore.website.externals.TextRenderer
import io.github.ayfri.kore.website.externals.use
import io.github.ayfri.kore.website.pages.PageNotFound
import org.jetbrains.compose.web.css.Style

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
	// `marked` is a module-level singleton, so its renderer is registered once instead of on every recomposition.
	remember {
		val textRenderer = object : TextRenderer() {
			override fun link(href: String?, title: String?, text: String): String {
				val titleAttribute = title?.let { " title=\"$it\"" }.orEmpty()
				return """<a href="$href"$titleAttribute class="link">$text</a>"""
			}

			override fun code(code: String, infoString: String, escaped: Boolean): String {
				val language = if (infoString.isEmpty()) "nohighlight" else "language-$infoString"
				return """<pre><code class="$language line-numbers">$code</code></pre>"""
			}
		}

		use(object : MarkedOptions {
			override var renderer: TextRenderer? = textRenderer
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
