package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle
import io.github.ayfri.kore.website.utils.lineClamp
import io.github.ayfri.kore.website.utils.lineHeight
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

/** Only `h2` and `h3`: with the deeper levels, the outline of a long page is several screens tall. */
private val headingsSelector = ".${MarkdownLayoutStyle.mainContent} :is(h2, h3).${MarkdownLayoutStyle.heading}"

/** Restarts the highlight animation, which only replays once the class is removed and the element reflowed in between. */
private fun Element.replayHighlight() {
	classList.remove(MarkdownLayoutStyle.highlight)
	(this as? HTMLElement)?.offsetWidth
	classList.add(MarkdownLayoutStyle.highlight)
}

private class TocHeading(val element: HTMLElement) {
	val id = element.id
	val nested = element.tagName == "H3"
	val text = element.textContent.orEmpty().trim()
}

@Composable
fun TableOfContents() {
	val currentPath = rememberPageContext().route.path
	var headings by remember { mutableStateOf(emptyList<TocHeading>()) }
	var activeHeadingId by remember { mutableStateOf<String?>(null) }
	var navElement by remember { mutableStateOf<HTMLElement?>(null) }

	LaunchedEffect(currentPath) {
		headings = document.querySelectorAll(headingsSelector).asList().map { TocHeading(it as HTMLElement) }.filter { it.id.isNotEmpty() }
	}

	// `offsetTop` forces a layout, so heading positions are measured once and refreshed on resize, never on scroll.
	var offsets by remember(headings) { mutableStateOf(headings.map { it.id to it.element.offsetTop }) }

	window.onEvents(
		"scroll" to { _: Event ->
			val scrollPosition = window.scrollY + ACTIVE_HEADING_OFFSET_PX
			activeHeadingId = offsets.lastOrNull { (_, top) -> top <= scrollPosition }?.first
		},
		"resize" to { _: Event -> offsets = headings.map { it.id to it.element.offsetTop } },
		/** By id and not `querySelector(hash)`, which throws on an id starting with a digit like `#1-start-from-the-closest-existing-feature`. */
		"hashchange" to { _: Event -> document.getElementById(window.location.hash.drop(1))?.replayHighlight() },
		key = offsets,
	)

	/** Sets the outline's own `scrollTop`: `scrollIntoView` would also move the window, cutting the reader's smooth scroll. */
	LaunchedEffect(activeHeadingId) {
		val nav = navElement ?: return@LaunchedEffect
		val entry = nav.querySelector(".${TableOfContentsStyle.activeEntry}") as? HTMLElement ?: return@LaunchedEffect
		val bottom = entry.offsetTop + entry.offsetHeight
		if (entry.offsetTop < nav.scrollTop) nav.scrollTop = entry.offsetTop.toDouble()
		else if (bottom > nav.scrollTop + nav.clientHeight) nav.scrollTop = (bottom - nav.clientHeight).toDouble()
	}

	Style(TableOfContentsStyle)

	if (headings.isEmpty()) return

	Nav({
		attr("aria-label", "On this page")
		classes(TableOfContentsStyle.container)
		ref {
			navElement = it
			onDispose { navElement = null }
		}
	}) {
		P({
			classes(TableOfContentsStyle.title)
		}) { Text("On this page") }

		Ul({
			classes(TableOfContentsStyle.list)
		}) {
			headings.forEach { heading ->
				Li {
					A("#${heading.id}", {
						classes(TableOfContentsStyle.entry)
						if (heading.nested) classes(TableOfContentsStyle.nestedEntry)
						if (heading.id == activeHeadingId) classes(TableOfContentsStyle.activeEntry)
						title(heading.text)
						onClick { heading.element.replayHighlight() }
					}) {
						Text(heading.text)
					}
				}
			}
		}
	}
}

data object TableOfContentsStyle : StyleSheet() {
	val container by style {
		borderLeft(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		flexShrink(0)
		marginTop(1.cssRem)
		/** The sticky offset plus the 5.5rem the go-to-top button takes under the outline. */
		maxHeight(calc { 100.vh - 10.5.cssRem })
		overflowY(Overflow.Auto)
		padding(0.25.cssRem, 0.25.cssRem, 0.25.cssRem, 1.cssRem)
		position(Position.Sticky)
		top(5.cssRem)
		width(14.cssRem)

		xlMax(self) {
			display(DisplayStyle.None)
		}
	}

	val entry by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.85.cssRem)
		lineClamp(2)
		lineHeight(1.35)
		overflow(Overflow.Hidden)
		padding(0.1.cssRem, 0.px)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		self + hover style {
			color(GlobalStyle.textColor)
		}
	}

	/** Declared after [entry] so its color wins the cascade at equal specificity. */
	val activeEntry by style {
		color(GlobalStyle.linkColor)

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}

	val list by style {
		listStyle(ListStyle.None)
		margin(0.px)
		padding(0.px)
	}

	val nestedEntry by style {
		paddingLeft(0.85.cssRem)
	}

	val title by style {
		color(GlobalStyle.textColor)
		fontSize(0.85.cssRem)
		fontWeight(600)
		margin(0.px, 0.px, 0.5.cssRem)
	}
}
