package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle
import io.github.ayfri.kore.website.utils.onEvents
import io.github.ayfri.kore.website.utils.transition
import io.github.ayfri.kore.website.utils.xlMax
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event

/** Distance above a heading at which it becomes the active entry. */
private const val ACTIVE_HEADING_OFFSET_PX = 100

private val headingsSelector = ".${MarkdownLayoutStyle.mainContent} .${MarkdownLayoutStyle.heading}"

/** Restarts the highlight animation, which only replays once the class is removed and the element reflowed in between. */
private fun Element.replayHighlight() {
	classList.remove(MarkdownLayoutStyle.highlight)
	(this as? HTMLElement)?.offsetWidth
	classList.add(MarkdownLayoutStyle.highlight)
}

private class TocHeading(val element: HTMLElement) {
	val id = element.id
	val indent = element.tagName.last().digitToInt() - 2
	val text = element.textContent.orEmpty().trim()
}

@Composable
fun TableOfContents() {
	val currentPath = rememberPageContext().route.path
	var headings by remember { mutableStateOf(emptyList<TocHeading>()) }
	var activeHeadingId by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(currentPath) {
		headings = document.querySelectorAll(headingsSelector).asList().map { TocHeading(it as HTMLElement) }
	}

	// `offsetTop` forces a layout, so heading positions are measured once and refreshed on resize, never on scroll.
	var offsets by remember(headings) { mutableStateOf(headings.map { it.id to it.element.offsetTop }) }

	window.onEvents(
		"scroll" to { _: Event ->
			val scrollPosition = window.scrollY + ACTIVE_HEADING_OFFSET_PX
			activeHeadingId = offsets.lastOrNull { (_, top) -> top <= scrollPosition }?.first
		},
		"resize" to { _: Event -> offsets = headings.map { it.id to it.element.offsetTop } },
		"hashchange" to { _: Event ->
			window.location.hash.takeIf(String::isNotEmpty)?.let { document.querySelector(it)?.replayHighlight() }
		},
		key = offsets,
	)

	Style(TableOfContentsStyle)

	Nav({
		classes(TableOfContentsStyle.container)
	}) {
		H4({
			classes(TableOfContentsStyle.title)
		}) { Text("On this page") }

		Ul {
			headings.forEach { heading ->
				Li({
					classes(TableOfContentsStyle.entry)
					if (heading.id == activeHeadingId) classes(TableOfContentsStyle.activeEntry)
					title(heading.text)
					style {
						marginLeft(heading.indent * 0.75.cssRem)
					}
					onClick {
						if (heading.id.isNotEmpty()) {
							window.location.hash = "#${heading.id}"
							heading.element.replayHighlight()
						}
					}
				}) {
					Text(heading.text)
				}
			}
		}
	}
}

data object TableOfContentsStyle : StyleSheet() {
	val container by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		maxHeight(72.5.vh)
		maxWidth(13.cssRem)
		overflowY(Overflow.Auto)
		padding(0.5.cssRem, 0.75.cssRem)
		position(Position.Sticky)
		top(10.vh)

		xlMax(self) {
			display(DisplayStyle.None)
		}

		"ul" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(1.px)
			paddingLeft(0.75.cssRem)
		}
	}

	val title by style {
		fontSize(1.cssRem)
		fontWeight(FontWeight.Bold)
		marginTop(0.px)
		marginBottom(0.5.cssRem)
	}

	val entry by style {
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		fontSize(0.8.cssRem)
		listStyle(ListStyle.None)
		overflow(Overflow.Hidden)
		padding(0.15.cssRem, 0.3.cssRem)
		borderRadius(GlobalStyle.roundingButton)
		textOverflow(TextOverflow.Ellipsis)
		transition(0.2.s, "color", "background-color")
		whiteSpace(WhiteSpace.NoWrap)
		userSelect(UserSelect.None)

		self + hover style {
			color(GlobalStyle.linkColor)
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val activeEntry by style {
		color(GlobalStyle.linkColor)
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
	}
}
