package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.compose.css.zIndex
import com.varabyte.kobweb.silk.components.icons.mdi.MdiMenu
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.mdMin
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

val tabs = mapOf(
	"Docs" to "$GITHUB_LINK/wiki",
	"About" to GITHUB_LINK
)

@Composable
fun HeaderButton(name: String, link: String) = Div {
	A(link, name) {
		classes(HeaderStyle.headerLink)
	}
}

@Composable
fun AltHeaderButton(name: String, link: String, target: ATarget? = null) = LinkButton(name, link, target)

@Composable
fun Header() {
	Style(HeaderStyle)

	Header({
		classes(HeaderStyle.header)
	}) {
		Div({
			classes(HeaderStyle.linksListDesktop)
		}) {
			org.jetbrains.compose.web.dom.A("/") {
				Img("/logo.png", "Kore Logo") {
					classes(HeaderStyle.logo)
				}
			}

			tabs.forEach { (name, link) -> HeaderButton(name, link) }
		}

		Div({
			classes(HeaderStyle.githubLink)
		}) {
			AltHeaderButton("GitHub", GITHUB_LINK, ATarget.Blank)
		}

		Div({
			classes(HeaderStyle.mobileMenu)
		}) {
			val openMenuInputName = "open-menu"

			Label(forId = openMenuInputName, {
				classes(HeaderStyle.mobileMenuLabel)
			}) {
				MdiMenu()
			}

			Input(InputType.Checkbox) {
				id(openMenuInputName)
				hidden()
				classes(HeaderStyle.mobileMenuInput)
			}

			Div({
				classes(HeaderStyle.linksListMobile)
			}) {
				tabs.forEach { (name, link) -> HeaderButton(name, link) }
			}

			org.jetbrains.compose.web.dom.A("/") {
				Img("/logo.png", "Kore Logo") {
					classes(HeaderStyle.logo)
				}
			}
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

	val linksListDesktop by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.5.cssRem)

		mdMax(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val mobileMenu by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)

		child(self, type("a")) style {
			position(Position.Absolute)
			left(50.percent)
			transform {
				translateX((-45).percent)
			}
			top(1.5.cssRem)
		}

		mdMin(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	val mobileMenuLabel by style {
		zIndex(10)

		className("material-icons") style {
			fontSize(2.2.cssRem)
		}

		mdMin(self) {
			display(DisplayStyle.None)
		}
	}

	val mobileMenuInput by style {
		display(DisplayStyle.None)

		adjacent(self + checked, type("div")) style {
			display(DisplayStyle.Flex)
			top(0.cssRem)
		}

		adjacent(self + not(checked), type("div")) style {
		}
	}

	val linksListMobile by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		flexDirection(FlexDirection.Column)
		position(Position.Absolute)
		gap(1.5.cssRem)
		left(0.px)
		paddingTop(7.cssRem)
		paddingBottom(1.cssRem)
		width(100.percent)
		top((-13.5).cssRem)

		transition(0.35.s, AnimationTimingFunction.EaseInOut, "top")
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

	val githubLink by style {
		mdMax(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	val logo by style {
		marginRight(2.cssRem)
		width(8.cssRem)

		mdMax(self) {
			marginRight(0.px)
			width(7.cssRem)
		}
	}
}
