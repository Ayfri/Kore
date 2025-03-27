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
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private fun StyleScope.indentation(level: Int) = marginLeft(level * 1.5.cssRem)

sealed class DocNode(val level: Int) {
	class EntryNode(val entry: DocArticle, level: Int, val isGroup: Boolean = false) : DocNode(level)
	class GroupNode(val name: String, level: Int) : DocNode(level)
}

@Composable
private fun DocNode.Render(currentURL: String) {
	when (this) {
		is DocNode.EntryNode -> Entry(entry, level, entry.path == currentURL, isGroup)
		is DocNode.GroupNode -> GroupEntry(name, level)
	}
}

@Composable
fun Entry(article: DocArticle, level: Int, selected: Boolean = false, isGroup: Boolean = false) = Li {
	A(article.path, article.navTitle) {
		style {
			if (level > 1) {
				indentation(level - 1)
			}
		}

		classes(DocTreeStyle.entry, DocTreeStyle.articleEntry)
		if (isGroup) classes(DocTreeStyle.groupArticleEntry)
		title(article.navTitle)
		if (selected) classes(DocTreeStyle.selected)
	}
}

@Composable
fun GroupEntry(name: String, level: Int) = Li({
	style {
		if (level > 1) {
			indentation(level - 1)
		}
	}
	classes(DocTreeStyle.entry, DocTreeStyle.groupEntry)
	title(name)
}) {
	Span(name)
}

@Composable
fun DocTree() {
	Style(DocTreeStyle)

	val context = rememberPageContext().route
	val entries = docEntries
	val currentURL = context.path

	val nodes = buildList<DocNode> {
		val sortedEntries = entries.sortedWith(Comparator { a, b ->
			val slugsA = a.slugs.drop(1)
			val slugsB = b.slugs.drop(1)
			val maxDepth = minOf(slugsA.size, slugsB.size)

			for (i in 0 until maxDepth) {
				val slugA = slugsA[i]
				val slugB = slugsB[i]

				val posA = if (i == slugsA.lastIndex) a.position else null
				val posB = if (i == slugsB.lastIndex) b.position else null

				val posCompare = compareValues(posA ?: Int.MAX_VALUE, posB ?: Int.MAX_VALUE)
				if (posCompare != 0) return@Comparator posCompare

				val slugCompare = compareValues(slugA.lowercase(), slugB.lowercase())
				if (slugCompare != 0) return@Comparator slugCompare
			}

			val lengthCompare = compareValues(slugsA.size, slugsB.size)
			if (lengthCompare != 0) return@Comparator lengthCompare

			return@Comparator compareValues(a.navTitle, b.navTitle)
		})

		val processedGroups = mutableSetOf<String>()

		sortedEntries.forEach { entry ->
			val slugs = entry.slugs.drop(1)
			val level = slugs.size

			if (slugs.size > 1) {
				for (i in 0 until slugs.size - 1) {
					val groupPathSlugs = slugs.take(i + 1)
					val groupPath = groupPathSlugs.joinToString("/")
					if (groupPath !in processedGroups) {
						processedGroups.add(groupPath)
						val groupName = slugs[i].kebabCaseToTitleCamelCase()

						val groupHasDirectEntry = entries.any { e -> e.slugs.drop(1) == groupPathSlugs }

						if (!groupHasDirectEntry) {
							add(DocNode.GroupNode(groupName, i + 1))
						}
					}
				}
			}

			val isGroup = entries.any { other ->
				other != entry &&
					other.slugs.size > slugs.size &&
					other.slugs.drop(1).take(slugs.size) == slugs
			}
			add(DocNode.EntryNode(entry, level, isGroup))

			if (isGroup) {
				processedGroups.add(slugs.joinToString("/"))
			}
		}
	}

	Div({
		id("doc-tree")
	}) {
		H2 {
			Text("Pages")
		}

		Ul({
			classes(DocTreeStyle.list)
		}) {
			nodes.forEach { node ->
				node.Render(currentURL)
			}
		}
	}
}

object DocTreeStyle : StyleSheet() {
	@OptIn(ExperimentalComposeWebApi::class)
	val list by style {
		listStyle(ListStyleType.None)
		marginRight(1.cssRem)
		marginTop(0.px)
		padding(0.8.cssRem)

		height(100.percent)
		left(0.px)
		position(Position.Sticky)
	}

	val entry by style {
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Block)
		fontSize(1.15.cssRem)
		padding(0.35.cssRem)
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

	val groupArticleEntry by style {}
}
