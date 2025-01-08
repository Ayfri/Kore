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

private fun StyleScope.indentation(level: Int) = marginLeft(level * 2.cssRem)

sealed class DocNode(val level: Int) {
	class EntryNode(val entry: DocArticle, level: Int, val isGroup: Boolean = false) : DocNode(level)
	class GroupNode(val name: String, level: Int, val hasEntry: Boolean = false) : DocNode(level)
}

@Composable
private fun DocNode.Render(currentURL: String) {
	when (this) {
		is DocNode.EntryNode -> Entry(entry, entry.path == currentURL, isGroup)
		is DocNode.GroupNode -> if (!hasEntry) GroupEntry(name, level - 1)
	}
}

@Composable
fun Entry(article: DocArticle, selected: Boolean = false, isGroup: Boolean = false) = Li {
	A(article.path, article.navTitle) {
		style {
			if (article.slugs.size > 2) {
				indentation(article.slugs.size - 2)
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
		if (level > 0) {
			indentation(level)
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

	// Pre-compute the tree structure
	val nodes = buildList {
		// First sort all entries by position and title
		val sortedEntries = entries.sortedWith(
			compareBy<DocArticle> { it.position ?: Int.MAX_VALUE }
				.thenBy { it.navTitle }
		)

		// Track groups we've already processed
		val processedGroups = mutableSetOf<String>()

		sortedEntries.forEach { entry ->
			val slugs = entry.slugs.drop(1)
			val level = slugs.size

			// For entries with multiple slugs, add group nodes
			if (slugs.size > 1) {
				// Process all but the last slug as potential groups
				for (i in 0 until slugs.size - 1) {
					val groupPath = slugs.take(i + 1).joinToString("/")
					if (groupPath !in processedGroups) {
						processedGroups.add(groupPath)
						val groupName = slugs[i].kebabCaseToTitleCamelCase()
						// Check if this group has a corresponding entry
						val hasEntry = entries.any { it.slugs.drop(1).last() == slugs[i] }
						add(DocNode.GroupNode(groupName, i + 1, hasEntry))
					}
				}
			}

			// Add the entry node, checking if it's also a group
			val isGroup = entries.any { other ->
				other != entry &&
					other.slugs.drop(1).size > slugs.size &&
					other.slugs.drop(1).take(slugs.size) == slugs
			}
			add(DocNode.EntryNode(entry, level, isGroup))
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
		padding(0.8.cssRem)
		marginTop(0.px)
		marginRight(1.cssRem)

		height(100.percent)
		position(Position.Sticky)
		left(0.px)
	}

	val entry by style {
		display(DisplayStyle.Block)
		padding(0.35.cssRem)
		fontSize(1.15.cssRem)

		borderRadius(GlobalStyle.roundingButton)
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

	val groupArticleEntry by style {}
}
