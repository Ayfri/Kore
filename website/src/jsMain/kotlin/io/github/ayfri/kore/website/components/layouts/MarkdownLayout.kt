package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.mdi.MdiChevronRight
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.components.doc.*
import io.github.ayfri.kore.website.utils.*
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div

@Composable
fun MarkdownLayout(content: @Composable () -> Unit) {
	Style(CalloutStyle)
	Style(MarkdownLayoutStyle)

	LaunchedEffect(Unit) {
		initMCFunctionHighlighting()
	}

	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter

	// Format dates for SEO
	val publishDate = markdownData["date-created"]?.get(0)?.let(::formatDate)
	val modifiedDate = markdownData["date-modified"]?.get(0)?.let(::formatDate)

	var onMobile by remember { mutableStateOf(false) }
	var revealed by remember { mutableStateOf(false) }

	onMobile = window.innerWidth < 768
	window.onresize = {
		onMobile = window.innerWidth < 768
	}

	val slugs = context.route.path.split("/").drop(1)

	// Set SEO metadata
	LaunchedEffect(Unit) {
		setHrefLang(context.route.path)
		setDates(publishDate, modifiedDate)
		setJsonLd(
			title = markdownData["title"]?.get(0) ?: "Untitled",
			description = markdownData["description"]?.get(0) ?: "",
			publishDate = publishDate,
			modifiedDate = modifiedDate,
			keywords = markdownData["keywords"]?.get(0) ?: "",
			path = context.route.path,
			slugs = slugs
		)
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

			Breadcrumbs(slugs.drop(1))

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

					PageNavigation(
						currentPath = context.route.path,
						publishDate = publishDate,
						modifiedDate = modifiedDate,
						editUrl = "https://github.com/Ayfri/Kore/edit/master/website/src/jsMain/resources/markdown/${context.markdown!!.path}"
					)
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
		backdropFilter(BackdropFilter.list(BackdropFilter.of(blur(5.px))))
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
		pointerEvents(PointerEvents.None)
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

		smMax(self) {
			marginTop(2.cssRem)
		}

		"ul" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(0.25.cssRem)
		}

		"table" style {
			borderCollapse(BorderCollapse.Separate)
			borderRadius(GlobalStyle.roundingButton)
			border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
			property("border-spacing", "0")
			marginY(1.cssRem)
			overflow(Overflow.Hidden)
			width(100.percent)
		}

		"thead" style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
		}

		"th, td" style {
			borderBottom(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
			borderRight(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
			padding(0.75.cssRem)
			textAlign(TextAlign.Left)
		}

		"th:last-child, td:last-child" style {
			borderRight(0.px)
		}

		"tr:last-child td" style {
			borderBottom(0.px)
		}

		"tr:nth-child(even)" style {
			backgroundColor(rgba(255, 255, 255, 0.05))
		}
	}

	val contentWrapper by style {
		marginRight(16.cssRem)

		smMax(self) {
			marginRight(0.px)
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

		child(self, type("code")) style {
			fontSize(1.1.em)
			paddingX(0.3.cssRem)
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
}
