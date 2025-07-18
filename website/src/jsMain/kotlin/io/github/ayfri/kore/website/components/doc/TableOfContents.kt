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
		maxHeight(80.vh - 4.cssRem)
		maxWidth(16.cssRem)
		overflowY(Overflow.Auto)
		padding(1.5.cssRem)
		position(Position.Fixed)
		right(1.cssRem)
		top(15.vh)

		smMax(self) {
			display(DisplayStyle.None)
		}

		"ul" style {
			paddingLeft(1.5.cssRem)
		}
	}

	val entry by style {
		cursor(Cursor.Pointer)
		padding(0.2.cssRem, 0.px)
		color(GlobalStyle.textColor)
		listStyle(ListStyle.None)
		transition(0.2.s, "color")
		overflow(Overflow.Hidden)
		textOverflow(TextOverflow.Ellipsis)
		whiteSpace(WhiteSpace.NoWrap)
		userSelect(UserSelect.None)

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}
}

@Composable
fun TableOfContents() {
	var headings by remember { mutableStateOf(listOf<HTMLElement>()) }

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

	Style(TableOfContentsStyle)

	Nav({
		classes(TableOfContentsStyle.container)
	}) {
		H3 { Text("On this page") }
		Ul {
			headings.forEach { heading ->
				val headingName = heading.innerText.removePrefix("link").trim()

				Li({
					classes(TableOfContentsStyle.entry)
					title(headingName)
					style {
						marginLeft((heading.tagName.last().toString().toInt() - 2) * 1.25.cssRem)
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
