package io.github.ayfri.kore.website.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.WEBSITE_GITHUB_LINK
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.P
import kotlin.js.Date
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun Footer() {
	Style(FooterStyle)

	Footer({
		classes(FooterStyle.footer)
	}) {
		Hr()

		P {
			Text("This website is ")

			A(WEBSITE_GITHUB_LINK, "open source") {
				target(ATarget.Blank)
			}
		}

		val currentYear = Date().getFullYear()
		P("Copyright © $currentYear - Ayfri. All rights reserved.")
	}
}

object FooterStyle : StyleSheet() {
	val footer by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		justifyContent(JustifyContent.Center)
		paddingBottom(2.cssRem)
		width(100.percent)

		"hr" style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
			border(0.px)
			height(1.px)
			width(100.percent)
		}

		"p" style {
			textAlign(TextAlign.Center)
			marginBottom(0.cssRem)
		}
	}
}
