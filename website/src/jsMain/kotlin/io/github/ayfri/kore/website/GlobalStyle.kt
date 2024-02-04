package io.github.ayfri.kore.website

import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*

object GlobalStyle : StyleSheet() {
	val backgroundColor = Color("#24282e")
	val secondaryBackgroundColor = Color("#181a1f")
	val tertiaryBackgroundColor = Color("#343a45")

	val altTextColor = Color("#e9e9e9")
	val linkColor = Color("#0597ba")
	val linkColorHover = Color("#23cae8")
	val textColor = Color("#fff")

	val roundingButton = 0.4.cssRem
	val roundingSection = 0.8.cssRem

	init {
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
}
