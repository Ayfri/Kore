package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.McSprite
import io.github.ayfri.kore.website.docSections
import io.github.ayfri.kore.website.utils.lineHeight
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

object FeatureGridStyle : StyleSheet() {
	val card by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		padding(1.25.cssRem)
	}

	val description by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		lineHeight(1.45)
		margin(0.px)
	}

	val grid by style {
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		margin(1.5.cssRem, 0.px)

		mdMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	/** No gap: the sprite brings its own right margin. */
	val header by style {
		display(DisplayStyle.Flex)
	}

	val pageLink by style {
		color(GlobalStyle.textColor)
		fontSize(0.9.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}

	val pages by style {
		columnGap(1.cssRem)
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		rowGap(0.35.cssRem)
	}

	/** 2rem makes the 1.5em sprite 48 px wide, three screen pixels per texture pixel, so the pixel art stays sharp. */
	val sprite by style {
		flexShrink(0)
		fontSize(2.cssRem)
		lineHeight(1)
	}

	val title by style {
		fontSize(1.1.cssRem)
		margin(0.px, 0.px, 0.25.cssRem)

		child(self, type("a")) style {
			color(GlobalStyle.textColor)
			textDecorationLine(TextDecorationLine.None)
			transition(0.2.s, "color")
		}

		child(self, type("a") + hover) style {
			color(GlobalStyle.linkColor)
		}
	}
}

/** One card per group of the `reference` section, listing every page of the group. */
@Composable
fun FeatureGrid() {
	Style(FeatureGridStyle)

	Div({
		classes(FeatureGridStyle.grid)
	}) {
		docSections.first { it.id == "reference" }.groups.forEach { group ->
			Div({
				classes(FeatureGridStyle.card)
			}) {
				Div({
					classes(FeatureGridStyle.header)
				}) {
					group.sprite?.let { sprite ->
						Span({
							classes(FeatureGridStyle.sprite)
						}) {
							McSprite(sprite, null)
						}
					}

					Div {
						H3({
							classes(FeatureGridStyle.title)
						}) {
							A(group.pages.first().path) { Text(group.title.orEmpty()) }
						}

						P({
							classes(FeatureGridStyle.description)
						}) {
							Text(group.description.orEmpty())
						}
					}
				}

				Div({
					classes(FeatureGridStyle.pages)
				}) {
					group.pages.forEach { page ->
						A(page.path, {
							classes(FeatureGridStyle.pageLink)
						}) {
							Text(page.navTitle)
						}
					}
				}
			}
		}
	}
}
