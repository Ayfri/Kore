package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle
import com.varabyte.kobweb.silk.components.icons.mdi.MdiArrowRight
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

data class ShowcaseProject(
	val title: String,
	val description: String,
	val imageUrl: String,
	val link: String,
)

@Composable
fun ShowcaseSection() {
	Style(ShowcaseSectionStyle)

	val showcaseProjects = listOf(
		ShowcaseProject(
			"Minecraft Adventure Map",
			"A complete adventure map with custom mechanics, all built using Kore's powerful API.",
			"/showcase/adventure-map.jpg",
			"https://github.com/example/adventure-map"
		),
		ShowcaseProject(
			"Server Minigames",
			"Collection of minigames for Minecraft servers with complex scoreboard systems.",
			"/showcase/minigames.jpg",
			"https://github.com/example/minigames"
		),
		ShowcaseProject(
			"Custom Crafting System",
			"Advanced crafting system with custom recipes and item interactions.",
			"/showcase/crafting.jpg",
			"https://github.com/example/crafting"
		)
	)

	Section({
		classes(ShowcaseSectionStyle.showcaseSection)
	}) {
		H2 {
			Text("Projects Built with Kore")
		}

		P({
			classes(ShowcaseSectionStyle.subtitle)
		}) {
			Text("Discover what developers are creating with Kore")
		}

		Div({
			classes(ShowcaseSectionStyle.showcaseGrid)
		}) {
			showcaseProjects.forEach { project ->
				Div({
					classes(ShowcaseSectionStyle.showcaseCard)
				}) {
					Div({
						classes(ShowcaseSectionStyle.showcaseImageContainer)
					}) {
						Img(src = project.imageUrl, alt = project.title) {
							classes(ShowcaseSectionStyle.showcaseImage)
						}
					}

					Div({
						classes(ShowcaseSectionStyle.showcaseContent)
					}) {
						H3 {
							Text(project.title)
						}

						P {
							Text(project.description)
						}

						A(href = project.link, attrs = {
							classes(ShowcaseSectionStyle.showcaseLink)
						}) {
							Text("View Project")
							MdiArrowRight(style = IconStyle.ROUNDED)
						}
					}
				}
			}
		}
	}
}

object ShowcaseSectionStyle : StyleSheet() {
	val showcaseSection by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		paddingY(6.cssRem)
		textAlign(TextAlign.Center)

		"h2" style {
			fontSize(3.cssRem)
			marginBottom(1.cssRem)
		}

		mdMax(type("h2")) {
			fontSize(2.5.cssRem)
		}
	}

	val subtitle by style {
		color(GlobalStyle.altTextColor)
		fontSize(1.2.cssRem)
		marginBottom(4.cssRem)
	}

	val showcaseGrid by style {
		display(DisplayStyle.Grid)
		gap(2.cssRem)
		gridTemplateColumns {
			repeat(3) { size(1.fr) }
		}
		marginX(auto)
		maxWidth(90.percent)

		lgMax(self) {
			gridTemplateColumns {
				repeat(2) { size(1.fr) }
			}
		}

		mdMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
		}
	}

	val showcaseCard by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingSection)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		overflow(Overflow.Hidden)
		transition(0.3.s, "translate", "box-shadow")

		hover(self) style {
			boxShadow(0.px, 10.px, 20.px, 0.px, rgba(0, 0, 0, 0.1))
			translateY((-5).px)
		}

		mdMax(self) {
			width(90.percent)
		}
	}

	val showcaseImageContainer by style {
		height(200.px)
		overflow(Overflow.Hidden)
		width(100.percent)
	}

	val showcaseImage by style {
		height(100.percent)
		objectFit(ObjectFit.Cover)
		transition(0.3.s, "scale")
		width(100.percent)

		hover(self) style {
			scale(1.05)
		}
	}

	val showcaseContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		flexGrow(1)
		padding(1.5.cssRem)
		textAlign(TextAlign.Left)

		"h3" style {
			fontSize(1.5.cssRem)
			marginBottom(1.cssRem)
			marginTop(0.px)
		}

		"p" style {
			color(GlobalStyle.altTextColor)
			flexGrow(1)
			fontSize(1.cssRem)
			lineHeight(1.6.number)
			marginBottom(1.5.cssRem)
		}
	}

	val showcaseLink by style {
		alignItems(AlignItems.Center)
		color(GlobalStyle.buttonBackgroundColor)
		display(DisplayStyle.Flex)
		fontSize(1.cssRem)
		fontWeight(600)
		gap(0.5.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")
		width(Width.FitContent)

		hover(self) style {
			color(GlobalStyle.buttonBackgroundColorHover)
		}

		className("material-icons-round") style {
			fontSize(1.2.cssRem)
		}
	}
}
