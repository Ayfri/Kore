package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

object TableOfContentsStyle : StyleSheet() {
	val container by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		maxHeight(72.5.vh)
		maxWidth(14.cssRem)
		overflowY(Overflow.Auto)
		padding(0.75.cssRem, 1.cssRem)
		position(Position.Sticky)
		top(10.vh)

		smMax(self) {
			display(DisplayStyle.None)
		}

		"ul" style {
			paddingLeft(1.cssRem)
		}
	}

	val title by style {
		fontSize(1.1.cssRem)
		fontWeight(FontWeight.Bold)
		marginTop(0.px)
		marginBottom(0.5.cssRem)
	}

	val entry by style {
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		fontSize(0.875.cssRem)
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

@Composable
fun TableOfContents() {
	var headings by remember { mutableStateOf(listOf<HTMLElement>()) }
	var activeHeadingId by remember { mutableStateOf<String?>(null) }

	LaunchedEffect(Unit) {
		headings = document.querySelectorAll(".${MarkdownLayoutStyle.mainContent} .${MarkdownLayoutStyle.heading}")
			.asList()
			.map { it as HTMLElement }
	}

	LaunchedEffect(Unit) {
		window.addEventListener("hashchange", {
			val hash = window.location.hash
			if (hash.isNotEmpty()) {
				val element = document.querySelector(hash)
				element?.classList?.remove(MarkdownLayoutStyle.highlight)
				element?.classList?.add(MarkdownLayoutStyle.highlight)
			}
		})
	}

	LaunchedEffect(Unit) {
		window.addEventListener("scroll", {
			val scrollPosition = window.scrollY + 100
			val currentHeadings = document.querySelectorAll(".${MarkdownLayoutStyle.mainContent} .${MarkdownLayoutStyle.heading}")
				.asList()
				.map { it as HTMLElement }

			var currentActive: String? = null
			for (heading in currentHeadings) {
				if (heading.offsetTop <= scrollPosition) {
					currentActive = heading.id
				} else {
					break
				}
			}
			activeHeadingId = currentActive
		})
	}

	Style(TableOfContentsStyle)

	Nav({
		classes(TableOfContentsStyle.container)
	}) {
		H4({
			classes(TableOfContentsStyle.title)
		}) { Text("On this page") }
		Ul {
			headings.forEach { heading ->
				val headingName = heading.innerText.removePrefix("link").trim()
				val isActive = heading.id == activeHeadingId

				Li({
					classes(TableOfContentsStyle.entry)
					if (isActive) classes(TableOfContentsStyle.activeEntry)
					title(headingName)
					style {
						marginLeft((heading.tagName.last().toString().toInt() - 2) * 0.75.cssRem)
					}
					onClick {
						val id = heading.id
						if (id.isNotEmpty()) {
							window.location.hash = "#$id"
							heading.classList.remove(MarkdownLayoutStyle.highlight)
							heading.offsetWidth
							heading.classList.add(MarkdownLayoutStyle.highlight)
						}
					}
				}) {
					Text(headingName)
				}
			}
		}
	}
}
