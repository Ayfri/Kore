@file:OptIn(ExperimentalComposeWebApi::class)

package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.css.functions.min
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFileText
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHash
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSearch
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.Search
import io.github.ayfri.kore.website.utils.inset
import io.github.ayfri.kore.website.utils.lineClamp
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.monoFont
import io.github.ayfri.kore.website.utils.monoLabel
import io.github.ayfri.kore.website.utils.onEvents
import io.github.ayfri.kore.website.utils.sansFont
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import kotlin.js.Json
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

private const val HITS_ID = "search-hits"
private const val MAX_PAGES = 6
private const val MAX_SECTIONS = 10

private val whitespace = Regex("\\s+")

/** One result row, a page or a section of a page. */
private class Hit(val aside: String, val description: String?, val href: String, val section: Boolean, val text: String)

/** A page with its lowercase haystacks computed once, so a keystroke only runs substring checks. */
private class PageCandidate(article: DocArticle) {
	val hit = Hit(
		aside = docLocation(article.path)?.let { listOfNotNull(it.section.title, it.group?.title).joinToString(" / ") }.orEmpty(),
		description = article.desc,
		href = article.path,
		section = false,
		text = article.navTitle,
	)
	val keywords = article.keywords.joinToString(" ").lowercase()
	val navTitle = article.navTitle.lowercase()
	val title = article.title.lowercase()
	val haystack = "$navTitle $title $keywords ${article.desc.lowercase()}"
}

/** The page title is part of the haystack, so "timers boss bar" finds the "Timer with Boss Bar" section of Timers. */
private class SectionCandidate(article: DocArticle, id: String, text: String) {
	val haystack = "$text ${article.navTitle}".lowercase()
	val heading = text.lowercase()
	val hit = Hit(aside = article.navTitle, description = null, href = "${article.path}#$id", section = true, text = text)
}

private val pageCandidates by lazy { docEntries.map(::PageCandidate) }

private var cachedSections: List<SectionCandidate>? = null

/** `/doc-search.json` is absent until the dev server restarts: a failed fetch gives null, the pages stay searchable and the next opening retries. */
private suspend fun loadSections() = runCatching {
	val response = window.fetch("/doc-search.json").await()
	check(response.ok) { "Failed to load doc-search.json: HTTP ${response.status}" }
	val data = JSON.parse<Json>(response.text().await())
	docEntries.flatMap { article ->
		data[article.path].unsafeCast<Array<Array<String>>?>()?.map { (id, text) -> SectionCandidate(article, id, text) }.orEmpty()
	}
}.getOrNull().also { cachedSections = it }

/** Pages by rank then nav order, followed by sections by rank then page order. */
private fun search(terms: List<String>, sections: List<SectionCandidate>): List<Hit> {
	if (terms.isEmpty()) return emptyList()
	val whole = terms.joinToString(" ")

	val pages = pageCandidates
		.filter { page -> terms.all { it in page.haystack } }
		.sortedBy { page ->
			when {
				page.navTitle.startsWith(whole) -> 0
				terms.all { it in page.navTitle || it in page.title } -> 1
				terms.all { it in page.keywords } -> 2
				else -> 3
			}
		}
		.take(MAX_PAGES)

	val matchingSections = sections
		.filter { section -> terms.all { it in section.haystack } }
		.sortedBy { section -> if (section.heading.startsWith(whole)) 0 else 1 }
		.take(MAX_SECTIONS)

	return pages.map { it.hit } + matchingSections.map { it.hit }
}

/** [onNavigate] runs when a result is opened: a section of the current page changes no document, so whatever covers the page has to close. */
@Composable
fun Search(onNavigate: () -> Unit) {
	val currentOnNavigate by rememberUpdatedState(onNavigate)
	var open by remember { mutableStateOf(false) }

	document.onEvents(
		"keydown" to { event: Event ->
			val keyEvent = event as KeyboardEvent
			val active = document.activeElement
			val typing = active is HTMLInputElement || active is HTMLTextAreaElement || active is HTMLSelectElement ||
				(active as? HTMLElement)?.isContentEditable == true
			if ((keyEvent.ctrlKey || keyEvent.metaKey) && keyEvent.key.lowercase() == "k") {
				keyEvent.preventDefault()
				open = !open
			} else if (keyEvent.key == "/" && !typing) {
				keyEvent.preventDefault()
				open = true
			} else if (keyEvent.key == "Escape" && open) {
				open = false
			}
		},
	)

	/**
	 * The palette lives in its own composition on `body`: the sidebar has a `view-transition-name`, which makes it the containing block of
	 * fixed descendants and keeps them under the site header.
	 */
	DisposableEffect(Unit) {
		val host = document.createElement("div")
		document.body!!.appendChild(host)
		val composition = renderComposable(host) {
			if (open) SearchPalette(
				onClose = { open = false },
				onOpenHit = {
					open = false
					currentOnNavigate()
				},
			)
		}
		onDispose {
			composition.dispose()
			host.remove()
		}
	}

	Style(SearchStyle)

	Search({
		classes(SearchStyle.container)
	}) {
		Button({
			classes(SearchStyle.trigger)
			onClick { open = true }
		}) {
			LucideSearch()
			Span({
				classes(SearchStyle.triggerLabel)
			}) {
				Text("Search docs")
			}
			Span({
				classes(SearchStyle.shortcutHint)
			}) {
				Text(if ("Mac" in window.navigator.userAgent) "⌘ K" else "Ctrl K")
			}
		}
	}
}

@Composable
private fun SearchPalette(onClose: () -> Unit, onOpenHit: () -> Unit) {
	var headings by remember { mutableStateOf(cachedSections.orEmpty()) }
	var listElement by remember { mutableStateOf<HTMLElement?>(null) }
	var query by remember { mutableStateOf("") }
	/** Bumped by the arrow keys only, so a hover never scrolls the list under the pointer. */
	var revealCount by remember { mutableStateOf(0) }

	val terms = remember(query) { query.trim().lowercase().split(whitespace).filter(String::isNotEmpty) }
	val hits = remember(terms, headings) { search(terms, headings) }
	val highlight = remember(terms) {
		Regex(terms.sortedByDescending(String::length).joinToString("|") { Regex.escape(it) }, RegexOption.IGNORE_CASE)
	}
	var selected by remember(hits) { mutableStateOf(0) }

	LaunchedEffect(Unit) {
		if (cachedSections == null) headings = loadSections().orEmpty()
	}

	LaunchedEffect(hits) {
		listElement?.scrollTop = 0.0
	}

	/** Sets the list's own `scrollTop`: `scrollIntoView` would also move the window. */
	LaunchedEffect(revealCount) {
		val list = listElement ?: return@LaunchedEffect
		val row = list.querySelector(".${SearchStyle.selectedRow}") as? HTMLElement ?: return@LaunchedEffect
		val top = if (selected == 0) 0 else row.offsetTop
		val bottom = row.offsetTop + row.offsetHeight
		if (top < list.scrollTop) list.scrollTop = top.toDouble()
		else if (bottom > list.scrollTop + list.clientHeight) list.scrollTop = (bottom - list.clientHeight).toDouble()
	}

	Div({
		classes(SearchStyle.backdrop, "search-open")
		onClick { onClose() }
	})

	Div({
		classes(SearchStyle.panel)
		attr("role", "dialog")
		attr("aria-modal", "true")
		attr("aria-label", "Search the documentation")
		/** The focusables are queried at each Tab, the result rows change with the query. */
		onKeyDown { event ->
			if (event.key != "Tab") return@onKeyDown
			val focusables = (event.nativeEvent.currentTarget as HTMLElement).querySelectorAll("a[href], input")
			val first = focusables.item(0) as HTMLElement
			val last = focusables.item(focusables.length - 1) as HTMLElement
			if (document.activeElement == if (event.shiftKey) first else last) {
				event.preventDefault()
				(if (event.shiftKey) last else first).focus()
			}
		}
	}) {
		Div({
			classes(SearchStyle.field)
		}) {
			LucideSearch()
			Input(InputType.Text) {
				classes(SearchStyle.input)
				if (hits.isNotEmpty()) attr("aria-activedescendant", "$HITS_ID-$selected")
				attr("aria-autocomplete", "list")
				attr("aria-controls", HITS_ID)
				attr("aria-expanded", hits.isNotEmpty().toString())
				attr("autocomplete", "off")
				attr("placeholder", "Search the documentation...")
				attr("role", "combobox")
				attr("value", query)
				onInput { event -> query = event.value }
				onKeyDown { event ->
					when (event.key) {
						"ArrowDown", "ArrowUp" -> {
							event.preventDefault()
							if (hits.isNotEmpty()) {
								selected = (selected + (if (event.key == "ArrowDown") 1 else -1) + hits.size) % hits.size
								revealCount++
							}
						}

						"Enter" -> hits.getOrNull(selected)?.let {
							window.location.href = it.href
							onOpenHit()
						}
					}
				}
				/** Closing the palette hands the focus back to what had it, the trigger or the page. */
				ref {
					val previous = document.activeElement as? HTMLElement
					it.focus()
					onDispose { previous?.focus() }
				}
			}
		}

		when {
			terms.isEmpty() -> P({
				classes(SearchStyle.message)
			}) {
				Text("Search pages and their sections by name or keyword.")
			}

			hits.isEmpty() -> P({
				classes(SearchStyle.message)
			}) {
				Text("No results for \"${query.trim()}\".")
			}

			else -> Div({
				classes(SearchStyle.list)
				id(HITS_ID)
				attr("role", "listbox")
				ref {
					listElement = it
					onDispose { listElement = null }
				}
			}) {
				hits.forEachIndexed { index, hit ->
					if (index == 0 || hits[index - 1].section != hit.section) {
						Div({
							classes(SearchStyle.groupLabel)
							attr("role", "presentation")
						}) {
							Text(if (hit.section) "On a page" else "Pages")
						}
					}

					SearchRow(hit, highlight, "$HITS_ID-$index", index == selected, onHover = { selected = index }, onOpen = onOpenHit)
				}
			}
		}

		Div({
			classes(SearchStyle.footer)
		}) {
			FooterHint("↑↓", "navigate")
			FooterHint("↵", "open")
			FooterHint("esc", "close")
		}
	}
}

@Composable
private fun SearchRow(hit: Hit, highlight: Regex, id: String, selected: Boolean, onHover: () -> Unit, onOpen: () -> Unit) {
	A(hit.href, {
		classes(SearchStyle.row)
		if (selected) classes(SearchStyle.selectedRow)
		id(id)
		attr("aria-selected", selected.toString())
		attr("role", "option")
		onClick { onOpen() }
		onMouseMove { onHover() }
	}) {
		if (hit.section) LucideHash() else LucideFileText()

		Div({
			classes(SearchStyle.rowBody)
		}) {
			Div({
				classes(SearchStyle.rowTitleLine)
			}) {
				Span({
					classes(SearchStyle.rowTitle)
				}) {
					Highlighted(hit.text, highlight)
				}
				Span({
					classes(SearchStyle.rowAside)
				}) {
					Text(hit.aside)
				}
			}

			hit.description?.let {
				Div({
					classes(SearchStyle.rowDescription)
				}) {
					Text(it)
				}
			}
		}
	}
}

@Composable
private fun Highlighted(text: String, terms: Regex) {
	var end = 0
	terms.findAll(text).forEach { match ->
		if (match.range.first > end) Text(text.substring(end, match.range.first))
		Span({
			classes(SearchStyle.match)
		}) {
			Text(match.value)
		}
		end = match.range.last + 1
	}
	if (end < text.length) Text(text.substring(end))
}

@Composable
private fun FooterHint(key: String, label: String) {
	Span({
		classes(SearchStyle.footerItem)
	}) {
		Span({
			classes(SearchStyle.shortcutHint)
		}) {
			Text(key)
		}
		Text(label)
	}
}

data object SearchStyle : StyleSheet() {
	init {
		/** The open palette's backdrop carries `search-open`, which locks the page scroll behind it. */
		selector("body:has(.search-open)") style {
			overflow(Overflow.Hidden)
		}
	}

	val backdrop by style {
		backgroundColor(rgba(4, 8, 12, 0.6))
		inset(0.px)
		position(Position.Fixed)
		zIndex(100)
	}

	val container by style {
		width(100.percent)
	}

	val field by style {
		alignItems(AlignItems.Center)
		borderBottom(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		display(DisplayStyle.Flex)
		gap(0.6.cssRem)
		padding(0.8.cssRem, 1.cssRem)

		child(self, type("svg")) style {
			color(GlobalStyle.altTextColor)
			flexShrink(0)
			fontSize(1.15.cssRem)
		}
	}

	val footer by style {
		borderTop(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		color(GlobalStyle.altTextColor)
		display(DisplayStyle.Flex)
		fontSize(0.75.cssRem)
		gap(1.cssRem)
		padding(0.55.cssRem, 1.cssRem)

		smMax(self) {
			display(DisplayStyle.None)
		}
	}

	val footerItem by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.35.cssRem)
	}

	val groupLabel by style {
		color(GlobalStyle.altTextColor)
		monoLabel(0.68.cssRem)
		padding(0.6.cssRem, 0.6.cssRem, 0.3.cssRem)
	}

	val input by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.textColor)
		fontSize(1.cssRem)
		/** The field keeps the focus the whole time the palette is open. Chrome ignores `outline-width` on its `auto` focus ring. */
		outlineStyle(LineStyle.None)
		sansFont()
		width(100.percent)
	}

	val list by style {
		minHeight(0.px)
		overflowY(Overflow.Auto)
		padding(0.4.cssRem)
		position(Position.Relative)
	}

	val match by style {
		color(GlobalStyle.linkColor)
		fontWeight(600)
	}

	val message by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		margin(0.px)
		padding(1.5.cssRem, 1.cssRem)
		textAlign(TextAlign.Center)
	}

	val panel by style {
		animation(GlobalStyle.rise) {
			duration(0.18.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}

		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(0.75.cssRem)
		boxShadow(0.px, 1.cssRem, 3.cssRem, color = rgba(0, 0, 0, 0.5))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		/** Centered by auto margins between `left: 0` and `right: 0`: a transform would fight the entry animation. */
		left(0.px)
		marginX(auto)
		marginY(0.px)
		maxHeight(76.vh)
		overflow(Overflow.Hidden)
		position(Position.Fixed)
		right(0.px)
		top(12.vh)
		width(min(40.cssRem, calc { 100.vw - 2.cssRem }))
		zIndex(101)
	}

	val row by style {
		alignItems(AlignItems.FlexStart)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		gap(0.65.cssRem)
		padding(0.55.cssRem, 0.6.cssRem)
		textDecorationLine(TextDecorationLine.None)

		self + hover style {
			color(GlobalStyle.textColor)
		}

		child(self, type("svg")) style {
			color(GlobalStyle.altTextColor)
			flexShrink(0)
			fontSize(1.cssRem)
			marginTop(0.2.cssRem)
		}
	}

	val rowAside by style {
		color(GlobalStyle.altTextColor)
		flexShrink(0)
		fontSize(0.75.cssRem)
		whiteSpace(WhiteSpace.NoWrap)

		smMax(self) {
			display(DisplayStyle.None)
		}
	}

	val rowBody by style {
		flexGrow(1)
		minWidth(0.px)
	}

	val rowDescription by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.8.cssRem)
		lineClamp(1)
		margin(0.15.cssRem, 0.px, 0.px)
		overflow(Overflow.Hidden)
	}

	val rowTitle by style {
		flexGrow(1)
		fontSize(0.95.cssRem)
		minWidth(0.px)
	}

	val rowTitleLine by style {
		alignItems(AlignItems.Baseline)
		display(DisplayStyle.Flex)
		gap(0.75.cssRem)
	}

	val selectedRow by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
	}

	val shortcutHint by style {
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(0.25.cssRem)
		color(GlobalStyle.altTextColor)
		flexShrink(0)
		fontSize(0.72.cssRem)
		monoFont()
		padding(0.05.cssRem, 0.35.cssRem)
		whiteSpace(WhiteSpace.NoWrap)

		smMax(self) {
			display(DisplayStyle.None)
		}
	}

	val trigger by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(1.cssRem)
		gap(0.4.cssRem)
		padding(0.5.cssRem)
		sansFont()
		textAlign(TextAlign.Left)
		transition(0.2.s, "border-color")
		width(100.percent)

		self + hover style {
			borderColor(GlobalStyle.linkColor)
		}

		child(self, type("svg")) style {
			flexShrink(0)
			fontSize(1.15.cssRem)
		}
	}

	val triggerLabel by style {
		flexGrow(1)
	}
}
