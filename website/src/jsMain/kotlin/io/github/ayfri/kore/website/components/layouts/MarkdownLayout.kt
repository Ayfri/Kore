package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.mdi.MdiChevronRight
import com.varabyte.kobweb.silk.components.icons.mdi.MdiKeyboardArrowUp
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.doc.DocSidebar
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList

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

	Nav({
		classes(MarkdownLayoutStyle.toc)
	}) {
		H3 { Text("On this page") }
		Ul {
			headings.forEach { heading ->
				Li({
					classes(MarkdownLayoutStyle.tocEntry)
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
					Text(heading.innerText.removePrefix("link").trim())
				}
			}
		}
	}
}

@Composable
fun PageNavigation(currentPath: String) {
	val currentIndex = docEntries.indexOfFirst { it.path == currentPath }
	if (currentIndex == -1) return

	Div({
		classes(MarkdownLayoutStyle.pageNav)
	}) {
		if (currentIndex > 0) {
			A(docEntries[currentIndex - 1].path, {
				classes(MarkdownLayoutStyle.prevPage)
			}) {
				Text("← ${docEntries[currentIndex - 1].navTitle}")
			}
		}

		if (currentIndex < docEntries.size - 1) {
			A(docEntries[currentIndex + 1].path, {
				classes(MarkdownLayoutStyle.nextPage)
			}) {
				Text("${docEntries[currentIndex + 1].navTitle} →")
			}
		}
	}
}

@Composable
fun GoToTopButton() {
	var visible by remember { mutableStateOf(false) }

	LaunchedEffect(Unit) {
		window.onscroll = {
			visible = window.scrollY > 300
		}
	}

	Button(
		{
			classes(MarkdownLayoutStyle.goToTopButton)
			if (visible) classes("visible")
			title("Go to top")
			onClick { window.scrollTo(0.0, 0.0) }
		}
	) {
		MdiKeyboardArrowUp()
	}
}

@Composable
fun MarkdownLayout(content: @Composable () -> Unit) {
	Style(MarkdownLayoutStyle)

	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter

	var onMobile by remember { mutableStateOf(false) }
	var revealed by remember { mutableStateOf(false) }

	onMobile = window.innerWidth < 768
	window.onresize = {
		onMobile = window.innerWidth < 768
	}

	PageLayout(markdownData["nav-title"]?.get(0) ?: "Untitled") {
		Button({
			classes(MarkdownLayoutStyle.revealButton)
			onClick { revealed = !revealed }
		}) {
			MdiChevronRight()
		}

		DocSidebar(revealed, onClose = { revealed = false })

		Div({
			classes(MarkdownLayoutStyle.content)
		}) {
			if (markdownData["description"] != null) {
				setDescription(markdownData["description"]!![0])
			}

			if (onMobile) {
				Div({
					classes(MarkdownLayoutStyle.overlay)
					if (revealed) classes("reveal")
				}) {}
			}

			Div({
				classes(MarkdownLayoutStyle.contentWrapper)
			}) {
				Div({
					classes(MarkdownLayoutStyle.mainContent)
				}) {
					content()

					Div({
						classes(MarkdownLayoutStyle.metadata)
					}) {
						markdownData["date-modified"]?.get(0)?.let { date ->
							P { Text("Last updated: $date") }
						}

						A(
							"https://github.com/Ayfri/Kore/edit/master/website/src/jsMain/resources/markdown/${context.markdown!!.path}",
							{
								classes(MarkdownLayoutStyle.editLink)
							}) {
							Text("Edit this page on GitHub")
						}
					}

					PageNavigation(context.route.path)
				}

				TableOfContents()
			}
		}

		GoToTopButton()
	}
}

object MarkdownLayoutStyle : StyleSheet() {
	init {
		id("root") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			minHeight(100.vh)
		}

		"html" style {
			scrollBehavior(ScrollBehavior.Smooth)
		}

		"main" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			flexGrow(1.0)
		}

		"h1" style {
			fontSize(3.cssRem)
			marginY(2.cssRem)
			textAlign(TextAlign.Center)
		}

		child(type("h1"), type("a")) style {
			display(DisplayStyle.None)
		}

		"h2" style {
			fontSize(2.cssRem)
			marginY(1.5.cssRem)
		}

		"blockquote" style {
			margin(0.px)
			paddingLeft(0.5.cssRem)
			borderLeft(3.px, LineStyle.Solid, rgba(1, 1, 1, 0.1))
		}

		"code" + not(attrContains("class", "language")) style {
			fontFamily("Consolas", "Monaco", "Andale Mono", "Ubuntu Mono", "monospace")
			fontSize(0.9.cssRem)
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			borderRadius(GlobalStyle.roundingButton)
			paddingX(0.2.cssRem)
			paddingY(0.1.cssRem)
		}

		child(type("div") + className("code-toolbar"), type("pre")) style {
			borderRadius(GlobalStyle.roundingButton)
		}

		smMax {
			"h1" {
				fontSize(2.5.cssRem)
				lineHeight(LineHeight.Normal)
				marginY(1.cssRem)
			}

			"h2" {
				fontSize(1.75.cssRem)
				marginY(0.75.cssRem)
				wordBreak(WordBreak.BreakAll)
			}

			"h3" {
				fontSize(1.25.cssRem)
				marginY(0.5.cssRem)
			}
		}
	}

	val revealButton by style {
		backdropFilter(blur(5.px))
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(0.px)
		borderTopRightRadius(GlobalStyle.roundingButton)
		borderBottomRightRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.None)
		padding(0.2.cssRem)
		position(Position.Fixed)
		top(4.5.cssRem)
		zIndex(10)

		smMax(self) {
			display(DisplayStyle.Block)
		}

		child(self, type("span")) style {
			fontSize(2.cssRem)
		}
	}

	val overlay by style {
		backgroundColor(rgba(0, 0, 0, 0.5))
		height(100.percent)
		left(0.px)
		opacity(0)
		position(Position.Fixed)
		top(0.px)
		transition(0.2.s, "opacity")
		width(100.percent)
		zIndex(5)

		self + className("reveal") style {
			opacity(1)
		}
	}

	val anchor by style {
		color(GlobalStyle.inactiveLinkColor)
		opacity(0.2)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "opacity", "color")

		self + hover style {
			opacity(1)
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginX(3.vw)
		marginBottom(2.cssRem)
		minHeight(100.percent)
		width(100.percent)
		overflowX(Overflow.Auto)
	}

	val contentWrapper by style {
		marginRight(16.cssRem)

		smMax(self) {
			marginRight(0.px)
		}
	}

	val highlightAnimation by keyframes {
		from { backgroundColor(rgba(0, 120, 215, 0.2)) }
		to { backgroundColor(rgba(0, 120, 215, 0)) }
	}

	val highlight by style {
		animation(highlightAnimation) {
			delay(0.6.s)
			duration(1.s)
			timingFunction(AnimationTimingFunction.Ease)
		}
	}

	val mainContent by style {
		minWidth(0.px)

		"img" style {
			borderRadius(GlobalStyle.roundingButton)
			marginY(0.5.cssRem)
			maxWidth(100.percent)
		}
	}

	val toc by style {
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

	val tocEntry by style {
		cursor(Cursor.Pointer)
		padding(0.2.cssRem, 0.px)
		color(GlobalStyle.textColor)
		listStyle(ListStyleType.None)
		transition(0.2.s, "color")
		overflow(Overflow.Hidden)
		textOverflow(TextOverflow.Ellipsis)
		whiteSpace(WhiteSpace.NoWrap)
		userSelect(UserSelect.None)

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}

	val metadata by style {
		marginTop(4.cssRem)
		paddingTop(1.cssRem)
		borderTop(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
	}

	val editLink by style {
		color(GlobalStyle.linkColor)
		textDecorationLine(TextDecorationLine.None)

		self + hover style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}

	val pageNav by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		marginTop(2.cssRem)
		gap(1.cssRem)
	}

	val prevPage by style {
		color(GlobalStyle.linkColor)
		textDecorationLine(TextDecorationLine.None)

		self + hover style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}

	val nextPage by style {
		color(GlobalStyle.linkColor)
		textDecorationLine(TextDecorationLine.None)
		marginLeft(autoLength)

		self + hover style {
			textDecorationLine(TextDecorationLine.Underline)
		}
	}

	val heading by style {
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.RowReverse)
		justifyContent(JustifyContent.FlexEnd)
		gap(0.5.cssRem)
		padding(0.5.cssRem)
		scrollMarginTop(1.5.cssRem)

		smMax(self) {
			padding(0.35.cssRem)
		}

		child(self + hover, className(anchor)) style {
			opacity(1)
		}
	}

	val goToTopButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(0.px)
		borderRadius(GlobalStyle.roundingButton)
		bottom(2.cssRem)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		height(3.cssRem)
		justifyContent(JustifyContent.Center)
		opacity(0)
		padding(0.px)
		position(Position.Fixed)
		right(2.cssRem)
		transition(0.3.s, "opacity", "translate")
		translateY(100.percent)
		width(3.cssRem)
		zIndex(10)

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}

		self + className("visible") style {
			opacity(1)
			translateY(0.percent)
		}

		child(self, type("span")) style {
			fontSize(2.cssRem)
		}
	}
}
