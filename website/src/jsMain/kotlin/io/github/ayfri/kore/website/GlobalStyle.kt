package io.github.ayfri.kore.website

import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.background
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.kore.website.utils.ScrollbarWidth
import io.github.ayfri.kore.website.utils.scrollbarColor
import io.github.ayfri.kore.website.utils.scrollbarWidth
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*

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
	val textColor = Color("#fff")

	val scrollbarThumbColor = Color("#ffffff99")
	val scrollbarBackgroundColor = Color("#181a1f")

	val borderColor = Color("#8c9ab1")
	val shadowColor = rgba(0, 0, 0, 0.7)

	val roundingButton = 0.4.cssRem
	val roundingSection = 0.8.cssRem

	init {
		universal {
			scrollbarColor(scrollbarThumbColor, scrollbarBackgroundColor)
			scrollbarWidth(ScrollbarWidth.THIN)
		}

		"body" {
			fontFamily(
				"-apple-system",
				"BlinkMacSystemFont",
				"Segoe UI",
				"Roboto",
				"Oxygen",
				"Ubuntu",
				"Cantarell",
				"Fira Sans",
				"Droid Sans",
				"Helvetica Neue",
				"sans-serif"
			)

			backgroundColor(backgroundColor)
			color(textColor)
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

	val altText by style {
		color(altTextColor)
	}

	val linkButton by style {
		color(textColor)
		background(tertiaryBackgroundColor)
		borderRadius(roundingButton)
		fontSize(1.5.cssRem)
		fontWeight(700)
		padding(0.5.cssRem, 1.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.3.s, "background")

		hover(self) style {
			background(secondaryBackgroundColor)
			color(textColor)
		}
	}
}
