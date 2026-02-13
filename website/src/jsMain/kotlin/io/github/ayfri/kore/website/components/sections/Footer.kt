package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.verticalAlign
import com.varabyte.kobweb.silk.components.icons.mdi.MdiFavorite
import io.github.ayfri.kore.website.DISCORD_LINK
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.WEBSITE_GITHUB_LINK
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*
import kotlin.js.Date

@Composable
fun Footer() {
	Style(FooterStyle)

	Footer({
		classes(FooterStyle.footer)
	}) {
		Div({
			classes(FooterStyle.content)
		}) {
			Div({
				classes(FooterStyle.section)
			}) {
				H3 { Text("Project") }
				A(GITHUB_LINK, "GitHub Repository", attrs = { target(ATarget.Blank) })
				A("$GITHUB_LINK/releases", "Releases", attrs = { target(ATarget.Blank) })
				A("$GITHUB_LINK/issues", "Issues", attrs = { target(ATarget.Blank) })
				A(WEBSITE_GITHUB_LINK, "Website Code", attrs = { target(ATarget.Blank) })
				A("https://github.com/Kore-Minecraft/Kore-Template", "Project Template", attrs = { target(ATarget.Blank) })
			}

			Div({
				classes(FooterStyle.section)
			}) {
				H3 { Text("Community") }
				A(DISCORD_LINK, "Discord", attrs = { target(ATarget.Blank) })
				A("https://kotlinlang.slack.com/archives/C066G9BF66A", "Slack #kore", attrs = { target(ATarget.Blank) })
				A("mailto:pierre.ayfri@gmail.com", "Contact", attrs = { target(ATarget.Blank) })
			}

			Div({
				classes(FooterStyle.section)
			}) {
				H3 { Text("Support") }
				A("https://www.buymeacoffee.com/ayfri", attrs = { target(ATarget.Blank) }) {
					Img(src = "https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png", attrs = {
						style {
							height(41.px)
							width(174.px)
						}
					})
				}
			}

			Div({
				classes(FooterStyle.section)
			}) {
				H3 { Text("Legal") }
				A("$GITHUB_LINK/blob/master/LICENSE", "GNU 3.0 License", attrs = {
					target(ATarget.Blank)
				})
			}
		}

		Hr({
			classes(FooterStyle.divider)
		})

		P({
			classes(FooterStyle.copyright)
		}) {
			val currentYear = Date().getFullYear()
			Text("Copyright © $currentYear - ")
			A("https://ayfri.com", "Ayfri") {
				title("Hello :)")
			}
			Text(". All rights reserved.")
			Br()
			Text("Built with ")
			MdiFavorite(Modifier.color(Color("#cf3f3f")).fontSize(0.9.cssRem).verticalAlign(VerticalAlign.Middle))
			Text(" with ")
			A("https://kobweb.varabyte.com/", "Kobweb", attrs = { target(ATarget.Blank) })
			Text(".")
		}
	}
}

object FooterStyle : StyleSheet() {
	val footer by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		paddingTop(4.cssRem)
		width(100.percent)

		smMax(self) {
			paddingTop(2.cssRem)
		}
	}

	val content by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns { repeat(4) { size(1.fr) } }
		gap(6.vw)
		justifyItems(JustifyItems.Center)
		marginBottom(3.cssRem)
		paddingX(15.vw)
		width(100.percent)

		lgMax(self) {
			gap(2.cssRem)
			paddingX(7.5.vw)
		}

		mdMax(self) {
			gridTemplateColumns { repeat(2) { size(1.fr) } }
			gap(3.cssRem)
			justifyItems(JustifyItems.Start)
			paddingX(10.vw)
		}

		smMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			gap(2.cssRem)
			paddingX(1.cssRem)
			textAlign(TextAlign.Center)
		}
	}

	val section by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.75.cssRem)
		width(Width.FitContent)

		"h3" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.25.cssRem)
			fontWeight(700)
			marginTop(0.px)
			marginBottom(0.5.cssRem)

			smMax(self) {
				fontSize(1.1.cssRem)
			}
		}

		"a" style {
			color(GlobalStyle.textColor)
			fontSize(1.cssRem)
			textDecorationLine(TextDecorationLine.None)
			transition(.2.s, "color")

			hover(self) style {
				color(GlobalStyle.altTextColor)
			}

			smMax(self + className("link")) {
				fontSize(0.9.cssRem)
			}
		}

		smMax(self) {
			gap(0.25.cssRem)
		}
	}

	val divider by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		border(0.px)
		height(1.px)
		margin(0.px)
		width(100.percent)
	}

	val copyright by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		marginY(1.5.cssRem)
		textAlign(TextAlign.Center)

		smMax(self) {
			fontSize(0.8.cssRem)
			marginY(1.cssRem)
		}

		type("a") style {
			color(GlobalStyle.altTextColor)
			hover(self) style {
				color(GlobalStyle.textColor)
			}
		}
	}
}
