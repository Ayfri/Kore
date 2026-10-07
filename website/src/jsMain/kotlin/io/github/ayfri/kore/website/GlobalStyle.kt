package io.github.ayfri.kore.website

import com.varabyte.kobweb.compose.css.ScrollbarWidth
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.scrollMarginTop
import com.varabyte.kobweb.compose.css.scrollbarWidth
import com.varabyte.kobweb.compose.css.setVariable
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.kore.website.utils.headingFont
import io.github.ayfri.kore.website.utils.sansFont
import io.github.ayfri.kore.website.utils.scrollbarColor
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

/** The site palette (`--landing-*`), set on `:root`. */
object LandingVars {
	val Accent by StyleVariable<CSSColorValue>()
	val AccentStrong by StyleVariable<CSSColorValue>()
	val Border by StyleVariable<CSSColorValue>()
	val Card by StyleVariable<CSSColorValue>()
	val Gold by StyleVariable<CSSColorValue>()
	val Muted by StyleVariable<CSSColorValue>()

	/** The surface of panes, tabs and code windows, a step above [Surface]. */
	val Pane by StyleVariable<CSSColorValue>()
	val Surface by StyleVariable<CSSColorValue>()
	val Text by StyleVariable<CSSColorValue>()
}

object GlobalStyle : StyleSheet() {
	val backgroundColor = Color("#24282e")
	val secondaryBackgroundColor = Color("#181a1f")
	val tertiaryBackgroundColor = Color("#343a45")

	val logoLeftColor = Color("#fec907")
	val logoRightColor = Color("#049bb2")

	val altTextColor = Color("#a7b5bd")
	val buttonBackgroundColor = Color("#05738c")
	val buttonBackgroundColorHover = Color("#0597ba")
	val linkColor = Color("#0597ba")
	val linkColorHover = Color("#23cae8")
	val inactiveLinkColor = Color("#a7b5bd")
	val textColor = Color("#fff")

	val scrollbarThumbColor = Color("#ffffff99")
	val scrollbarBackgroundColor = Color("#181a1f")

	val borderColor = Color("#8c9ab1")

	val roundingButton = 0.4.cssRem

	init {
		":root" style {
			setVariable(LandingVars.Accent, Color("#08b6d6"))
			setVariable(LandingVars.AccentStrong, Color("#1fd2f2"))
			setVariable(LandingVars.Border, rgba(151, 176, 202, 0.18))
			setVariable(LandingVars.Card, Color("#151c26"))
			setVariable(LandingVars.Gold, Color("#fec907"))
			setVariable(LandingVars.Muted, Color("#a6b4bd"))
			setVariable(LandingVars.Pane, Color("#141c26"))
			setVariable(LandingVars.Surface, Color("#0f141b"))
			setVariable(LandingVars.Text, Color("#f7f9fc"))
		}

		universal {
			scrollbarColor(scrollbarThumbColor, scrollbarBackgroundColor)
			scrollbarWidth(ScrollbarWidth.Thin)
		}

		"body" {
			backgroundColor(backgroundColor)
			color(textColor)
			sansFont()
		}

		":is(h1, h2, h3)" style {
			headingFont()
		}

		"html" style {
			scrollMarginTop(2.cssRem)
		}

		"html, body" style {
			margin(0.px)
			padding(0.px)
		}

		"a" style {
			color(linkColor)
			textDecorationLine(TextDecorationLine.None)
			transition(0.3.s, "color")

			hover(type("a")) style {
				color(linkColorHover)
			}
		}
	}

	/** The fade-up every entering panel, card and scene uses. */
	@OptIn(ExperimentalComposeWebApi::class)
	val rise by keyframes {
		from {
			opacity(0)
			transform { translateY(0.5.cssRem) }
		}

		to {
			opacity(1)
			transform { translateY(0.px) }
		}
	}
}
