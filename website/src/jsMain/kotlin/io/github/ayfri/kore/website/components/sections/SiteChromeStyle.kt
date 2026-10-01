package io.github.ayfri.kore.website.components.sections

import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.borderColor
import com.varabyte.kobweb.compose.css.boxSizing
import com.varabyte.kobweb.compose.css.functions.calc
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.paddingX
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto

/** The content column and outlined social links the site header and footer share. */
object SiteChromeStyle : StyleSheet() {
	val hoverBackground = rgba(255, 255, 255, 0.06)

	val container by style {
		boxSizing(BoxSizing.BorderBox)
		marginX(auto)
		maxWidth(100.percent)
		paddingX(1.25.cssRem)
		width(calc { 55.vw + 30.cssRem })
	}

	val outlineButton by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(GlobalStyle.roundingButton)
		boxSizing(BoxSizing.BorderBox)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.Center)
		transition(0.15.s, "background-color", "border-color", "color")

		hover(self) style {
			backgroundColor(hoverBackground)
			borderColor(rgba(151, 176, 202, 0.3))
			color(GlobalStyle.textColor)
		}
	}
}
