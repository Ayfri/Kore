package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

object PageNavigationStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		marginTop(2.cssRem)
		gap(1.cssRem)
	}

	val prevPage by style {
		color(GlobalStyle.linkColor)
		textDecorationLine(TextDecorationLine.None)

		self + hover style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}

	val nextPage by style {
		color(GlobalStyle.linkColor)
		marginLeft(autoLength)
		textDecorationLine(TextDecorationLine.None)

		self + hover style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}
}

@Composable
fun PageNavigation(currentPath: String) {
	val currentIndex = docEntries.indexOfFirst { it.path == currentPath }
	if (currentIndex == -1) return

	Style(PageNavigationStyle)

	Div({
		classes(PageNavigationStyle.container)
	}) {
		if (currentIndex > 0) {
			A(docEntries[currentIndex - 1].path, {
				classes(PageNavigationStyle.prevPage)
				title(docEntries[currentIndex - 1].navTitle)
			}) {
				Text("← ${docEntries[currentIndex - 1].navTitle}")
			}
		}

		if (currentIndex < docEntries.size - 1) {
			A(docEntries[currentIndex + 1].path, {
				classes(PageNavigationStyle.nextPage)
				title(docEntries[currentIndex + 1].navTitle)
			}) {
				Text("${docEntries[currentIndex + 1].navTitle} →")
			}
		}
	}
}
