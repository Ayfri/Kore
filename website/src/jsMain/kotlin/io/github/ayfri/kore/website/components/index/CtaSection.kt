package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.overflow
import com.varabyte.kobweb.compose.css.textAlign
import io.github.ayfri.kore.website.components.common.ButtonColor
import io.github.ayfri.kore.website.components.common.LinkButton
import io.github.ayfri.kore.website.utils.P
import io.github.ayfri.kore.website.utils.marginX
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
		padding(5.cssRem, 2.cssRem)
		position(Position.Relative)
		overflow(Overflow.Hidden)
		borderRadius(1.8.cssRem)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		property(
			"background",
			"radial-gradient(circle at 15% 15%, rgba(8, 182, 214, 0.28) 0%, rgba(8, 182, 214, 0) 45%), " +
				"radial-gradient(circle at 85% 10%, rgba(254, 201, 7, 0.22) 0%, rgba(254, 201, 7, 0) 40%), " +
				"linear-gradient(140deg, rgba(15, 20, 27, 0.95) 0%, rgba(21, 28, 38, 0.95) 100%)"
		)

		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)
		marginX(6.vw)

		"h2" style {
			fontSize(2.5.cssRem)
			lineHeight(1.1.number)
			marginTop(0.px)
			marginBottom(1.2.cssRem)
			maxWidth(45.cssRem)
		}

		mdMax(type("h2")) {
			fontSize(2.1.cssRem)
			width(95.percent)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.1.cssRem)
			marginBottom(2.cssRem)
			maxWidth(35.cssRem)
		}

		mdMax(type("p")) {
			width(95.percent)
		}
	}
}
