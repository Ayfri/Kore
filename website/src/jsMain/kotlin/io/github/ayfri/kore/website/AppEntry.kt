package io.github.ayfri.kore.website

import androidx.compose.runtime.Composable
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
	val renderer = object : TextRenderer() {
		override fun link(href: String?, title: String?, text: String) = """
			<a href="$href" ${title?.let { "title=$it" } ?: ""} class="link">$text</a>
		""".trimIndent()

		override fun code(code: String, infoString: String, escaped: Boolean): String {
			val language = if (infoString.isEmpty()) "nohighlight" else "language-$infoString"
			return """
				<pre><code class="$language line-numbers">$code</code></pre>
			""".trimIndent()
		}
	}

	use(object : MarkedOptions {
		override var renderer: TextRenderer? = renderer
	})


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
