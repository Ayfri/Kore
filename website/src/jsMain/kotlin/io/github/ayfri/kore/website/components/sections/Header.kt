package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.background
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Img

val tabs = mapOf(
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
fun AltHeaderButton(name: String, link: String, target: ATarget? = null) = Div {
	A(link, name) {
		classes(HeaderStyle.altLink)
		target?.let { target(it) }
	}
}

@Composable
fun Header() {
	Style(HeaderStyle)

	Header({
		classes(HeaderStyle.header)
	}) {
		Div({
			classes(HeaderStyle.linksList)
		}) {
			org.jetbrains.compose.web.dom.A("/") {
				Img("/logo.png", "Kore Logo") {
					classes(HeaderStyle.logo)
				}
			}

			tabs.forEach { (name, link) -> HeaderButton(name, link) }
		}

		Div {
			AltHeaderButton("GitHub", GITHUB_LINK, ATarget.Blank)
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
		padding(1.cssRem, 2.cssRem)

		"div" style {
			alignItems("center")
			display(DisplayStyle.Flex)
		}
	}

	val linksList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.5.cssRem)
	}

	val headerLink by style {
		color(GlobalStyle.textColor)
		fontSize(1.5.cssRem)
		fontWeight(700)
		textDecorationLine(TextDecorationLine.Underline)
		textDecorationColor(Color.transparent)
		transition(0.3.s, "text-decoration-color")

		hover(self) style {
			color(GlobalStyle.textColor)
			textDecorationColor(Color.currentColor)
		}
	}

	val logo by style {
		marginRight(2.cssRem)
		width(8.cssRem)
	}

	val altLink by style {
		color(GlobalStyle.textColor)
		background(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		fontSize(1.5.cssRem)
		fontWeight(700)
		padding(0.5.cssRem, 1.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.3.s, "background")

		hover(self) style {
			background(GlobalStyle.secondaryBackgroundColor)
			color(GlobalStyle.textColor)
		}
	}
}
