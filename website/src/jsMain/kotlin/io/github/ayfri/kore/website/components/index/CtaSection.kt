package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.textAlign
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.utils.P
import io.github.ayfri.kore.website.utils.marginY
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

		LinkButton("Get Started", "https://ayfri.com/articles/kore-introduction/", classes = arrayOf("primary"))
	}
}

object CtaSectionStyle : StyleSheet() {
	val cta by style {
		marginY(8.cssRem)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)

		"h2" style {
			fontSize(3.2.cssRem)
			marginY(2.5.cssRem)
			width(40.cssRem)
		}

		mdMax(type("h2")) {
			fontSize(2.5.cssRem)
			width(90.percent)
		}

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.2.cssRem)
			marginY(3.cssRem)
			width(30.cssRem)
		}

		mdMax(type("p")) {
			width(90.percent)
		}
	}
}
