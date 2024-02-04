package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.GITHUB_LINK
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.P
import io.github.ayfri.kore.website.utils.Span
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.textGradient
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Page
@Composable
fun HomePage() {
	PageLayout("Home") {
		Style(HomePageStyle)

		Section({
			classes(HomePageStyle.heroSection)
		}) {
			H1({
				classes(HomePageStyle.title)
			}) {
				Text("Rethink your datapack development experience with")
				Span("Kore")
			}

			P(
				"""
					Kore is a modern, open-source, and easy-to-use Kotlin library for Minecraft datapack development.
					You can use it to create your own Minecraft Datapacks, and without the need to write a single line of JSON or MCFunction.
				""".trimIndent(),
				HomePageStyle.subTitle
			)

			Div({
				classes(HomePageStyle.actions)
			}) {
				LinkButton("Get Started", "/docs/getting-started", classes = arrayOf("primary"))
				LinkButton("GitHub", GITHUB_LINK)
			}
		}

		P {
			Text("Welcome to Kore!")
		}
	}
}

object HomePageStyle : StyleSheet() {
	val titleFontSize = 4.cssRem

	val heroSection by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		textAlign(TextAlign.Center)

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.2.cssRem)
		}
	}

	val title by style {
		fontSize(titleFontSize)
		fontWeight(700)
		letterSpacing((-1.5).px)
		maxWidth(80.percent)
		marginY(4.cssRem)

		"span" style {
			fontWeight(900)
			marginLeft(titleFontSize / 4)
			textGradient(GlobalStyle.logoLeftColor, GlobalStyle.logoRightColor)
		}
	}

	val subTitle by style {
		maxWidth(90.percent)
	}

	val actions by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)

		"a" + className("primary") style {
			backgroundColor(GlobalStyle.buttonBackgroundColor)

			hover(self) style {
				backgroundColor(GlobalStyle.buttonBackgroundColorHover)
			}
		}
	}
}
