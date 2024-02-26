package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.PUBLIC_URL
import io.github.ayfri.kore.website.components.common.setCanonical
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setTitle
import io.github.ayfri.kore.website.components.sections.Footer
import io.github.ayfri.kore.website.components.sections.Header
import io.github.ayfri.kore.website.utils.loadPrism
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.dom.Main
import org.w3c.dom.url.URL

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	Style(PageLayoutStyle)
	Style(CodeThemeStyle)

	Header()

	Main({
		classes(PageLayoutStyle.main)
	}) {
		content()
	}

	Footer()

	val route = rememberPageContext().route
	val url = URL(PUBLIC_URL + route.path)
	url.search = ""
	url.hash = ""

	setCanonical(url.href)
	setTitle("$title - Kore, Kotlin library for making Datapacks")
	setDescription("Kore is a modern, open-source, and easy-to-use Kotlin library for Minecraft datapack development. It's designed to be simple, fast, and easy to use.")

	loadPrism()
}

object PageLayoutStyle : StyleSheet() {
	val main by style {
	}
}
