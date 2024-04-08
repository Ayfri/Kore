package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.Span
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ul

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
		classes(DocTreeStyle.list)
	}) {
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
	val list by style {
		listStyle(ListStyleType.None)
		padding(0.8.cssRem)
		marginRight(1.cssRem)

		height(100.vh)
		position(Position.Sticky)
		top(0.px)
		left(0.px)
	}

	val entry by style {
		display(DisplayStyle.Block)
		padding(0.5.cssRem)

		borderRadius(GlobalStyle.roundingButton)
		fontSize(1.2.cssRem)

		color(GlobalStyle.textColor)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val groupEntry by style {
		color(GlobalStyle.altTextColor)
		userSelect(UserSelect.None)
	}

	val articleEntry by style {
		transition(0.2.s, "background-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}
	}

	val selected by style {
		color(GlobalStyle.linkColor)
	}
}
