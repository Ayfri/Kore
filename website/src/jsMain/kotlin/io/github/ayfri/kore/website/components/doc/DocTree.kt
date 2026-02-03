package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.mdi.MdiChevronRight
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

private fun StyleScope.indentation(level: Int) = marginLeft(level * 1.5.cssRem)

// Define the order of top-level groups
private val groupOrder = listOf("guides", "commands", "data-driven", "concepts", "helpers", "advanced")

private fun getGroupPriority(slug: String): Int {
	val index = groupOrder.indexOf(slug.lowercase())
	return if (index >= 0) index else groupOrder.size
}

/**
 * Returns the ordered list of DocArticle entries as they appear in the DocTree.
 * This is used by PageNavigation to determine previous/next pages.
 */
fun getOrderedDocEntries(): List<DocArticle> {
	return docEntries.sortedWith(Comparator { a, b ->
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

			if (i == 0 && slugsA.size > 1 && slugsB.size > 1) {
				val groupCompare = compareValues(getGroupPriority(slugA), getGroupPriority(slugB))
				if (groupCompare != 0) return@Comparator groupCompare
			}

			val slugCompare = compareValues(slugA.lowercase(), slugB.lowercase())
			if (slugCompare != 0) return@Comparator slugCompare
		}

		val lengthCompare = compareValues(slugsA.size, slugsB.size)
		if (lengthCompare != 0) return@Comparator lengthCompare

		return@Comparator compareValues(a.navTitle, b.navTitle)
	})
}

sealed class DocNode(val level: Int, val groupPath: String) {
	class EntryNode(val entry: DocArticle, level: Int, val isGroup: Boolean = false, groupPath: String) : DocNode(
		level,
		groupPath
	)

	class GroupNode(val name: String, level: Int, groupPath: String, val collapsedByDefault: Boolean = false) : DocNode(
		level,
		groupPath
	)
}

@Composable
private fun DocNode.Render(
	currentURL: String,
	collapsedGroups: Set<String>,
	onToggleCollapse: (String) -> Unit,
) {
	when (this) {
		is DocNode.EntryNode -> Entry(
			entry,
			level,
			entry.path == currentURL,
			isGroup,
			groupPath,
			collapsedGroups,
			onToggleCollapse
		)

		is DocNode.GroupNode -> GroupEntry(name, level, groupPath, collapsedGroups, onToggleCollapse)
	}
}

@Composable
fun Entry(
	article: DocArticle,
	level: Int,
	selected: Boolean = false,
	isGroup: Boolean = false,
	groupPath: String,
	collapsedGroups: Set<String>,
	onToggleCollapse: (String) -> Unit,
) = Li {
	Div({
		classes(DocTreeStyle.entryRow)
		if (isGroup) classes(DocTreeStyle.collapsibleRow)
		style {
			if (level > 1) {
				indentation(level - 1)
			}
		}
	}) {
		if (isGroup) {
			val isCollapsed = groupPath in collapsedGroups
			Div({
				classes(DocTreeStyle.collapseToggle)
				if (isCollapsed) classes(DocTreeStyle.collapsed)
				onClick { onToggleCollapse(groupPath) }
			}) {
				MdiChevronRight()
			}
		}
		A(article.path, article.navTitle) {
			classes(DocTreeStyle.entry, DocTreeStyle.articleEntry)
			if (isGroup) classes(DocTreeStyle.groupArticleEntry)
			title(article.navTitle)
			if (selected) classes(DocTreeStyle.selected)
		}
	}
}

@Composable
fun GroupEntry(
	name: String,
	level: Int,
	groupPath: String,
	collapsedGroups: Set<String>,
	onToggleCollapse: (String) -> Unit,
) = Li {
	val isCollapsed = groupPath in collapsedGroups
	Div({
		classes(DocTreeStyle.entryRow, DocTreeStyle.collapsibleRow)
		if (level == 1) classes(DocTreeStyle.topLevelGroup)
		style {
			if (level > 1) {
				indentation(level - 1)
			}
		}
		onClick { onToggleCollapse(groupPath) }
	}) {
		Div({
			classes(DocTreeStyle.collapseToggle)
			if (isCollapsed) classes(DocTreeStyle.collapsed)
		}) {
			MdiChevronRight()
		}
		Span({
			classes(DocTreeStyle.entry, DocTreeStyle.groupEntry)
			title(name)
		}) {
			Text(name)
		}
	}
}

@Composable
fun DocTree() {
	Style(DocTreeStyle)

	val context = rememberPageContext().route
	val entries = docEntries
	val currentURL = context.path

	// Define the order of top-level groups
	val groupOrder = listOf("guides", "commands", "data-driven", "concepts", "helpers", "advanced")

	fun getGroupPriority(slug: String): Int {
		val index = groupOrder.indexOf(slug.lowercase())
		return if (index >= 0) index else groupOrder.size
	}

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

				// Apply group priority for top-level groups
				if (i == 0 && slugsA.size > 1 && slugsB.size > 1) {
					val groupCompare = compareValues(getGroupPriority(slugA), getGroupPriority(slugB))
					if (groupCompare != 0) return@Comparator groupCompare
				}

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
							add(DocNode.GroupNode(groupName, i + 1, groupPath))
						}
					}
				}
			}

			val isGroup = entries.any { other ->
				other != entry &&
					other.slugs.size > slugs.size &&
					other.slugs.drop(1).take(slugs.size) == slugs
			}
			add(DocNode.EntryNode(entry, level, isGroup, slugs.joinToString("/")))

			if (isGroup) {
				processedGroups.add(slugs.joinToString("/"))
			}
		}
	}

	// Initialize collapsed groups with those marked as collapsed by default
	val defaultCollapsed = nodes
		.filterIsInstance<DocNode.GroupNode>()
		.filter { it.collapsedByDefault }
		.map { it.groupPath }
		.toSet()

	var collapsedGroups by remember { mutableStateOf(defaultCollapsed) }

	fun isNodeVisible(node: DocNode): Boolean {
		val nodePath = node.groupPath
		for (collapsedPath in collapsedGroups) {
			if (nodePath != collapsedPath && nodePath.startsWith("$collapsedPath/")) {
				return false
			}
		}
		return true
	}

	Div({
		id("doc-tree")
	}) {
		P({
			classes(DocTreeStyle.title)
		}) {
			Text("Pages")
		}

		Ul({
			classes(DocTreeStyle.list)
		}) {
			var seenFirstTopLevelGroup = false
			nodes.forEach { node ->
				if (isNodeVisible(node)) {
					if (node is DocNode.GroupNode && node.level == 1) seenFirstTopLevelGroup = true
					node.Render(currentURL, collapsedGroups, { path ->
						collapsedGroups = if (path in collapsedGroups) {
							collapsedGroups - path
						} else {
							collapsedGroups + path
						}
					})
				}
			}
		}
	}
}

object DocTreeStyle : StyleSheet() {
	val title by style {
		fontSize(1.5.cssRem)
		fontWeight(FontWeight.Bold)
		marginY(0.5.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val list by style {
		listStyle(ListStyle.None)
		left(0.px)
		marginTop(0.px)
		maxHeight(72.5.vh)
		overflowY(Overflow.Auto)
		padding(0.5.cssRem)
		position(Position.Sticky)

		smMax(self) {
			maxHeight(75.vh)
		}
	}

	val entry by style {
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Block)
		fontSize(1.cssRem)
		padding(0.2.cssRem, 0.4.cssRem)
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

	val entryRow by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
	}

	val collapsibleRow by style {
		cursor(Cursor.Pointer)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val collapseToggle by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		width(1.cssRem)
		height(1.cssRem)
		transition(0.2.s, "transform")
		transform { rotate(90.deg) }
		userSelect(UserSelect.None)

		self + className("material-icons") style {
			fontSize(1.2.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val collapsed by style {
		transform { rotate(0.deg) }
	}

	val topLevelGroup by style {
		marginTop(0.75.cssRem)
	}
}
