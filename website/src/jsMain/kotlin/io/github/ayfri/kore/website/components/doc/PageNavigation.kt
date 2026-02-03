package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

object PageNavigationStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginTop(2.cssRem)
		paddingTop(1.5.cssRem)
		borderTop(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		gap(1.5.cssRem)
	}

	val metadataSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
	}

	val metadataDates by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.5.cssRem)
	}

	val metadataItem by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.25.cssRem)
	}

	val metadataLabel by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.75.cssRem)
		fontWeight(500)
		textTransform(TextTransform.Uppercase)
		property("letter-spacing", "0.05em")
	}

	val metadataValue by style {
		color(GlobalStyle.textColor)
		fontSize(0.9.cssRem)
		fontWeight(500)
	}

	val editLink by style {
		color(GlobalStyle.linkColor)
		fontSize(0.9.cssRem)
		textDecorationLine(TextDecorationLine.None)

		self + hover style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}

	val navLinks by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		gap(1.cssRem)
	}

	val navLink by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.linkColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.25.cssRem)
		padding(1.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val prevPage by style {
		alignItems(AlignItems.FlexStart)
	}

	val nextPage by style {
		alignItems(AlignItems.FlexEnd)
		marginLeft(autoLength)
	}

	val navLabel by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.8.cssRem)
	}

	val navTitle by style {
		fontSize(1.cssRem)
		fontWeight(600)
	}
}

@Composable
fun PageNavigation(currentPath: String, publishDate: String?, modifiedDate: String?, editUrl: String) {
	val orderedEntries = getOrderedDocEntries()
	val currentIndex = orderedEntries.indexOfFirst { it.path == currentPath }
	if (currentIndex == -1) return

	Style(PageNavigationStyle)

	Div({
		classes(PageNavigationStyle.container)
	}) {
		Div({
			classes(PageNavigationStyle.metadataSection)
		}) {
			Div({
				classes(PageNavigationStyle.metadataDates)
			}) {
				publishDate?.let { date ->
					Span({
						classes(PageNavigationStyle.metadataItem)
						attr("datetime", date)
					}) {
						Span({ classes(PageNavigationStyle.metadataLabel) }) { Text("Published") }
						Span({ classes(PageNavigationStyle.metadataValue) }) { Text(date) }
					}
				}

				modifiedDate?.let { date ->
					Span({
						classes(PageNavigationStyle.metadataItem)
						attr("datetime", date)
					}) {
						Span({ classes(PageNavigationStyle.metadataLabel) }) { Text("Last updated") }
						Span({ classes(PageNavigationStyle.metadataValue) }) { Text(date) }
					}
				}
			}

			A(editUrl, {
				classes(PageNavigationStyle.editLink)
			}) {
				Text("Edit this page on GitHub")
			}
		}

		Div({
			classes(PageNavigationStyle.navLinks)
		}) {
			if (currentIndex > 0) {
				val prevEntry = orderedEntries[currentIndex - 1]
				A(prevEntry.path, {
					classes(PageNavigationStyle.navLink, PageNavigationStyle.prevPage)
					title(prevEntry.navTitle)
				}) {
					Span({ classes(PageNavigationStyle.navLabel) }) { Text("← Previous") }
					Span({ classes(PageNavigationStyle.navTitle) }) { Text(prevEntry.navTitle) }
				}
			}

			if (currentIndex < orderedEntries.size - 1) {
				val nextEntry = orderedEntries[currentIndex + 1]
				A(nextEntry.path, {
					classes(PageNavigationStyle.navLink, PageNavigationStyle.nextPage)
					title(nextEntry.navTitle)
				}) {
					Span({ classes(PageNavigationStyle.navLabel) }) { Text("Next →") }
					Span({ classes(PageNavigationStyle.navTitle) }) { Text(nextEntry.navTitle) }
				}
			}
		}
	}
}
