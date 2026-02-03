package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
@Page
fun PageNotFound() {
	val context = rememberPageContext()
	val currentPath = context.route.path

	// Redirect to docs home if the path starts with /docs/
	LaunchedEffect(currentPath) {
		if (currentPath.startsWith("/docs/")) {
			context.router.navigateTo("/docs/home")
		}
	}

	// If redirecting to docs, don't show the 404 page
	if (currentPath.startsWith("/docs/")) return

	PageLayout("404 - Page Not Found") {
		Style(PageNotFoundStyle)

		Div({
			classes(PageNotFoundStyle.container)
		}) {
			Div({
				classes(PageNotFoundStyle.errorCode)
			}) {
				Text("404")
			}

			Div({
				classes(PageNotFoundStyle.content)
			}) {
				H1({
					classes(PageNotFoundStyle.title)
				}) {
					Text("Oops! Page not found")
				}

				P({
					classes(PageNotFoundStyle.description)
				}) {
					Text("The page you're looking for doesn't exist or has been moved.")
				}

				Div({
					classes(PageNotFoundStyle.buttons)
				}) {
					LinkButton("Go Home", "/")
					LinkButton("Go Back", "javascript:history.back()")
				}
			}
		}
	}
}

object PageNotFoundStyle : StyleSheet() {
	init {
		id("root") style {
			minHeight(100.vh)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
		}

		"main" {
			display(DisplayStyle.Flex)
			flexGrow(1.0)
			alignItems(AlignItems.Center)
			justifyContent(JustifyContent.Center)
			padding(2.cssRem)
		}
	}

	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)
		gap(1.cssRem)
		maxWidth(600.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val errorCode by style {
		fontSize(12.cssRem)
		fontWeight(900)
		lineHeight(1.number)
		backgroundImage(
			linearGradient(135.deg) {
				add(GlobalStyle.logoLeftColor)
				add(GlobalStyle.logoRightColor)
			}
		)
		property("-webkit-background-clip", "text")
		property("background-clip", "text")
		color(Color.transparent)
		userSelect(UserSelect.None)

		mdMax(self) {
			fontSize(8.cssRem)
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
	}

	val title by style {
		fontSize(2.cssRem)
		fontWeight(700)
		color(GlobalStyle.textColor)
		margin(0.px)

		mdMax(self) {
			fontSize(1.5.cssRem)
		}
	}

	val description by style {
		fontSize(1.1.cssRem)
		color(GlobalStyle.altTextColor)
		margin(0.px)
		maxWidth(400.px)
		lineHeight(1.6.number)

		mdMax(self) {
			fontSize(1.cssRem)
		}
	}

	val buttons by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(1.cssRem)
		marginTop(1.cssRem)

		mdMax(self) {
			flexDirection(FlexDirection.Column)
			width(100.percent)
		}

		type("a") style {
			transition(0.2.s, "transform")
		}

		type("a") + hover style {
			property("transform", "translateY(-2px)")
		}
	}
}
