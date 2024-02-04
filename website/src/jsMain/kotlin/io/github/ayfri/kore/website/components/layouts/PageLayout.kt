package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.css.margin
import io.github.ayfri.kore.website.components.sections.Header
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.dom.Main
import kotlinx.browser.document

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	Style(PageLayoutStyle)
	Header()
	Main({
		classes(PageLayoutStyle.main)
	}) {
		content()
	}

	LaunchedEffect(title) {
		document.title = "$title - Kore"
	}
}

object PageLayoutStyle : StyleSheet() {
	val main by style {
		margin(topBottom = 0.cssRem, leftRight = autoLength)
		maxWidth(80.cssRem)
	}
}
