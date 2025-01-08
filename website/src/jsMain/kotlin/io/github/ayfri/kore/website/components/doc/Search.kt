package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.mdi.MdiSearch
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.Search
import io.github.ayfri.kore.website.utils.transition
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

@Composable
fun Search() {
	var query by remember { mutableStateOf("") }
	var showResults by remember { mutableStateOf(false) }
	val context = rememberPageContext()

	Style(SearchStyle)

	Search({
		classes(SearchStyle.container)
	}) {
		Div({
			classes(SearchStyle.inputContainer)
		}) {
			MdiSearch()
			Input(InputType.Search) {
				classes(SearchStyle.input)
				attr("placeholder", "Search documentation...")
				attr("value", query)
				onInput { event -> query = event.value }
				onFocus { showResults = true }
			}
		}

		if (showResults && query.isNotEmpty()) {
			val results = docEntries.filter {
				it.title.contains(query, ignoreCase = true) ||
					it.desc.contains(query, ignoreCase = true) ||
					it.keywords.any { keyword -> keyword.contains(query, ignoreCase = true) }
			}

			Div({
				classes(SearchStyle.results)
			}) {
				if (results.isEmpty()) {
					P({
						classes(SearchStyle.noResults)
					}) {
						Text("No results found")
					}
				} else {
					results.take(5).forEach { entry ->
						A(entry.path, {
							classes(SearchStyle.result)
							onClick {
								context.router.navigateTo(entry.path)
								showResults = false
								query = ""
							}
						}) {
							H4 { Text(entry.title) }
							P { Text(entry.desc) }
						}
					}
				}
			}
		}
	}

	// Click outside to close results
	LaunchedEffect(Unit) {
		document.addEventListener("click", { event ->
			val target = event.target
			if (target !is HTMLElement || target.closest(".${SearchStyle.container}") == null) {
				showResults = false
			}
		})
	}
}

object SearchStyle : StyleSheet() {
	val container by style {
		position(Position.Relative)
		width(100.percent)
		marginBottom(1.cssRem)
	}

	val inputContainer by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
		padding(0.8.cssRem)
		transition(0.2.s, "border-color")

		self + focus style {
			borderColor(GlobalStyle.linkColor)
		}

		child(self, type("span")) style {
			color(GlobalStyle.altTextColor)
			fontSize(1.5.cssRem)
		}
	}

	val input by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.textColor)
		fontSize(1.cssRem)
		outline(0.px)
		width(100.percent)
	}

	val results by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		boxShadow(0.px, 4.px, 12.px, 0.px, rgba(0, 0, 0, 0.1))
		left(0.px)
		marginTop(0.5.cssRem)
		maxHeight(400.px)
		overflowY(Overflow.Auto)
		position(Position.Absolute)
		right(0.px)
		top(100.percent)
		zIndex(100)
	}

	val result by style {
		backgroundColor(Color.transparent)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Block)
		padding(1.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}

		child(self, type("h4")) style {
			color(GlobalStyle.linkColor)
			margin(0.px)
			marginBottom(0.2.cssRem)
		}

		child(self, type("p")) style {
			color(GlobalStyle.altTextColor)
			fontSize(0.9.cssRem)
			margin(0.px)
		}
	}

	val noResults by style {
		color(GlobalStyle.altTextColor)
		margin(0.px)
		padding(1.cssRem)
		textAlign(TextAlign.Center)
	}
}
