package io.github.ayfri.kore.website

import org.jetbrains.compose.web.css.*

object GlobalStyle : StyleSheet() {
	val backgroundColor = Color("#24282e")
	val secondaryBackgroundColor = Color("#181a1f")
	val tertiaryBackgroundColor = Color("#343a45")

	val altTextColor = Color("#f0f0f0")
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
	}

	val altText by style {
		color(altTextColor)
	}
}
