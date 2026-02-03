package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.components.common.*
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

	val route = rememberPageContext().route
	val baseUrl = AppGlobals["websiteUrl"] ?: "https://kore.ayfri.com"
	val url = URL(baseUrl + route.path)
	url.search = ""
	url.hash = ""

	setCanonical(url.href)
	setTitle("$title - Kore, library for making Datapacks")
	setDescription("Kore is a modern, open-source, and easy-to-use Kotlin library for Minecraft datapack development. It's designed to be simple, fast, and easy to use.")
	setType("website")

	setTwitterCard("summary_large_image")
	setTwitterCreator("@Ayfri_")

	setImage("$baseUrl/logo.png")

	Header()

	Main({
		classes(PageLayoutStyle.main)
	}) {
		content()
	}

	Footer()


	loadPrism()
}

object PageLayoutStyle : StyleSheet() {
	val main by style {
	}
}
