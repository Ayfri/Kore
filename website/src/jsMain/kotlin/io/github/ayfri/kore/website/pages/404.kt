package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.utils.P
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingX
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun PageNotFound() = PageLayout("404") {
	Style(PageNotFoundStyle)

	H1 {
		Text("Sorry, this page doesn't exist.")
	}
	P("The page you're looking for doesn't exist (yet ?) or has been moved.")
	LinkButton("Go back", "javascript:history.back()")
}

object PageNotFoundStyle : StyleSheet() {
	init {
		id("root") style {
			minHeight(100.vh)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
		}

		"main" {
			paddingX(5.vw)
			marginY(auto)
			display(DisplayStyle.Grid)
			placeItems(AlignItems.Center, JustifyItems.Center)
			textAlign(TextAlign.Center)

			"p" {
				marginBottom(2.5.cssRem)
			}
		}

		"h1" {
			fontSize(4.cssRem)

			mdMax(desc(type("main"), type("h1"))) {
				fontSize(3.cssRem)
			}
		}
	}
}
