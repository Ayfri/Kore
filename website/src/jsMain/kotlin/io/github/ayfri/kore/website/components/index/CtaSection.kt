package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.ButtonColor
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.utils.P
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.mdMax
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

@Composable
fun CtaSection() {
	Style(CtaSectionStyle)

	Div({
		classes(CtaSectionStyle.cta)
	}) {
		H2 {
			Text("Build your first datapack in Kotlin")
		}

		P("Craft your first datapack with Kore and enjoy the power of Kotlin for Minecraft development.")

		LinkButton("Get Started", "/docs/getting-started", color = ButtonColor.PRIMARY)
	}
}

object CtaSectionStyle : StyleSheet() {
	val cta by style {
		padding(12.cssRem, 2.cssRem)
		property(
			"background",
			"linear-gradient(135deg, ${GlobalStyle.logoRightColor.alpha(0.15)} 0%, ${GlobalStyle.logoLeftColor.alpha(0.05)} 100%)"
		)
		border(1.px, LineStyle.Solid, GlobalStyle.logoRightColor.alpha(0.2))

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)

		"h2" style {
			fontSize(2.4.cssRem)
			lineHeight(1.1.number)
			marginTop(0.px)
			marginBottom(1.5.cssRem)
			maxWidth(45.cssRem)
		}

		mdMax(type("h2")) {
			fontSize(2.1.cssRem)
			width(95.percent)
		}

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.1.cssRem)
			marginBottom(2.5.cssRem)
			maxWidth(35.cssRem)
		}

		mdMax(type("p")) {
			width(95.percent)
		}
	}
}
