package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.functions.calc
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
	"Docs" to "/docs/home",
	"About" to GITHUB_LINK,
	"Updates" to "/updates",
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
		Div({ classes(HeaderStyle.container) }) {
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
				LinkButton("GitHub", GITHUB_LINK, ATarget.Blank, icon = {
					Img(src = "/github-mark-white.svg", alt = "GitHub Logo") {
						classes(HeaderStyle.githubLogo)
					}
				})
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
}

object HeaderStyle : StyleSheet() {
	val header by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		height(4.5.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		padding(0.5.cssRem, 1.cssRem)

		position(Position.Sticky)
		top(0.px)
		property("backdrop-filter", "saturate(150%) blur(8px)")
		backgroundColor(rgba(24, 26, 31, 0.6))
		property("border-bottom", "1px solid ${GlobalStyle.tertiaryBackgroundColor}")
		zIndex(50)

		"div" style {
			alignItems("center")
			display(DisplayStyle.Flex)
		}
	}

	val container by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		property("margin-left", "auto")
		property("margin-right", "auto")
		width(calc { 55.vw + 30.cssRem })

		maxWidth(100.percent)
	}

	val linksListDesktop by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)

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
			top(1.cssRem)
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

	val logo by style {
		marginRight(1.2.cssRem)
		width(6.8.cssRem)

		mdMax(self) {
			marginRight(0.px)
			width(6.2.cssRem)
		}
	}

	val linksListMobile by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		flexDirection(FlexDirection.Column)
		position(Position.Absolute)
		gap(1.cssRem)
		left(0.px)
		paddingTop(5.cssRem)
		paddingBottom(1.cssRem)
		width(100.percent)
		top((-16).cssRem)

		transition(0.35.s, AnimationTimingFunction.EaseInOut, "top")
	}

	val headerLink by style {
		color(GlobalStyle.textColor)
		fontSize(1.3.cssRem)
		fontWeight(700)
		textDecorationLine(TextDecorationLine.Underline)
		textDecorationColor(Color.transparent)
		transition(0.2.s, "text-decoration-color")

		hover(self) style {
			color(GlobalStyle.textColor)
			textDecorationColor(GlobalStyle.linkColorHover)
		}
	}

	val githubLink by style {
		"a" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			gap(0.5.cssRem)
			justifyContent(JustifyContent.Center)
			fontSize(1.3.cssRem)
			padding(0.4.cssRem, 0.8.cssRem)
			borderRadius(GlobalStyle.roundingButton)
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			transition(0.2.s, "background")

			hover(type("a")) style {
				backgroundColor(GlobalStyle.secondaryBackgroundColor)
			}
		}

		mdMax(type("div") + self) {
			display(DisplayStyle.None)
		}
	}

	val githubLogo by style {
		height(1.4.cssRem)
		width(1.4.cssRem)
	}
}
