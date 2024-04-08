package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.HTMLButtonElement
import kotlinx.browser.document

private fun StyleScope.indentation(level: Int) = marginLeft(level * 2.cssRem)

@Composable
fun Entry(article: DocArticle, selected: Boolean = false) = Li {
	A(article.path, article.navTitle) {
		style {
			if (article.slugs.size > 2) {
				indentation(article.slugs.size - 2)
			}
		}

		classes(DocTreeStyle.entry, DocTreeStyle.articleEntry)
		if (selected) classes(DocTreeStyle.selected)
	}
}

@Composable
fun GroupEntry(name: String, level: Int) = Li({
	style {
		if (level > 0) {
			indentation(level)
		}
	}
	classes(DocTreeStyle.entry, DocTreeStyle.groupEntry)
}) {
	Span(name)
}

@Composable
fun DocTree() {
	Style(DocTreeStyle)

	val context = rememberPageContext().route
	val entries = docEntries
	val currentURL = context.path


	Ul({
		id("doc-tree")
		classes(DocTreeStyle.list)
	}) {
		Button({
			classes(DocTreeStyle.revealButton)

			onClick {
				val docTree = document.querySelector("#doc-tree")
				docTree?.classList?.toggle("reveal")
				it.currentTarget.unsafeCast<HTMLButtonElement>().textContent =
					if (docTree?.classList?.contains("reveal") == true) "<" else ">"
			}
		}) {
			Text(">")
		}

		// Separate the entries that are not in a group.
		val (simpleEntriesWithoutGroup, otherEntries) = entries.partition {
			it.middleSlugs.isEmpty() && entries.none { entry -> entry.middleSlugs.contains(it.slugs.last()) }
		}
		// Display these entries first.
		simpleEntriesWithoutGroup.forEach { entry ->
			Entry(entry, entry.path == currentURL)
		}

		// Then group the entries by their middle slugs.
		val presentedGroups = mutableSetOf<String>()
		otherEntries.groupBy { it.middleSlugs.joinToString("/") }.forEach { (slug, groupEntries) ->
			if (slug in presentedGroups || slug.isEmpty()) return@forEach

			presentedGroups += slug
			val slugName = slug.split("/").last()
			if (slugName in entries.map { it.slugs.last() }) {
				entries.find { it.slugs.last() == slug }?.let { entry ->
					Entry(entry, entry.path == currentURL)
				}
			} else {
				GroupEntry(slugName.kebabCaseToTitleCamelCase(), slug.count { it == '/' })
			}

			val sortedEntries = groupEntries.sortedBy { it.slugs.size }.filter { it.slugs.last() != slugName }
			sortedEntries.forEach { entry ->
				Entry(entry, entry.path == currentURL)
			}
		}
	}
}

object DocTreeStyle : StyleSheet() {
	val revealButton by style {
		position(Position.Absolute)
		left(100.percent)
		padding(0.4.cssRem, 0.8.cssRem)
		top(0.px)

		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderTopRightRadius(GlobalStyle.roundingButton)
		borderBottomRightRadius(GlobalStyle.roundingButton)
		borderStyle(LineStyle.None)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		fontSize(1.2.cssRem)
		fontWeight(FontWeight.Bold)

		smMin(self) {
			display(DisplayStyle.None)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val list by style {
		listStyle(ListStyleType.None)
		padding(0.8.cssRem)
		marginRight(1.cssRem)

		height(100.percent)
		position(Position.Sticky)
		top(0.px)
		left(0.px)

		smMax(self) {
			position(Position.Fixed)
			top(Top.Unset)
			height(Height.FitContent)
			zIndex(10)

			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			borderRadius(GlobalStyle.roundingSection)
			borderTopRightRadius(0.px)
			transition(0.3.s, "transform")
			transform {
				translateX((-100).percent)
			}

			self + className("reveal") style {
				transform {
					translateX(0.percent)
				}
			}
		}
	}

	val entry by style {
		display(DisplayStyle.Block)
		padding(0.5.cssRem)

		borderRadius(GlobalStyle.roundingButton)
		fontSize(1.2.cssRem)

		color(GlobalStyle.textColor)
		transition(0.2.s, "color", "background-color")
		whiteSpace(WhiteSpace.NoWrap)
	}

	val groupEntry by style {
		color(GlobalStyle.altTextColor)
		userSelect(UserSelect.None)
	}

	val articleEntry by style {
		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val selected by style {
		color(GlobalStyle.linkColor)
	}
}
