package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.kore.website.utils.A
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Img

val tabs = mapOf(
	"Home" to "/",
	"Docs" to "/docs",
	"About" to "/about"
)

@Composable
fun HeaderButton(name: String, link: String) = Div {
	A(link, name) {
		classes(HeaderStyle.headerLink)
	}
}

@Composable
fun Header() {
	Style(HeaderStyle)

	Header({
		classes(HeaderStyle.header)
	}) {
		Div {
			Img("/logo.png", "Kore Logo")
			tabs.forEach { (name, link) -> HeaderButton(name, link) }
		}

		Div {

		}
	}
}

object HeaderStyle : StyleSheet() {
	val header by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		height(5.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(1.cssRem)

		"div" style {
			alignItems("center")
			display(DisplayStyle.Flex)
		}
	}

	val headerLink by style {
		color(Color("#fff"))
		fontSize(1.5.cssRem)
		fontWeight(700)
		margin(0.cssRem, 1.cssRem)
		textDecorationLine(TextDecorationLine.None)

		hover(self) style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}
}
