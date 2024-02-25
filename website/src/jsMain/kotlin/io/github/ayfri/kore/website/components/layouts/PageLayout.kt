package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.components.sections.Footer
import io.github.ayfri.kore.website.components.sections.Header
import io.github.ayfri.kore.website.utils.loadPrism
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.dom.Main
import kotlinx.browser.document

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

	LaunchedEffect(title) {
		document.title = "$title - Kore"
	}

	loadPrism()
}

object PageLayoutStyle : StyleSheet() {
	val main by style {
	}
}
