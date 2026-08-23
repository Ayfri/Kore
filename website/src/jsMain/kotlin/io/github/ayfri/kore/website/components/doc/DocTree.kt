package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.util.kebabCaseToTitleCamelCase
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.mdi.MdiChevronRight
import com.varabyte.kobweb.silk.components.icons.mdi.MdiUnfoldLess
import com.varabyte.kobweb.silk.components.icons.mdi.MdiUnfoldMore
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import kotlinx.browser.sessionStorage
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.events.Event

private const val DOC_TREE_SCROLL_KEY = "doc-tree:scroll-top"
private const val DOC_TREE_COLLAPSED_GROUPS_KEY = "doc-tree:collapsed-groups"

/** The doc slugs of an entry, without the leading `docs` segment. */
private val DocArticle.docSlugs get() = slugs.drop(1)

/** Every strict ancestor group path of [slugs], e.g. `a/b/c` yields `a` then `a/b`. */
private fun ancestorPathsOf(slugs: List<String>) = List(maxOf(slugs.size - 1, 0)) { slugs.take(it + 1).joinToString("/") }

private fun parseCollapsedGroups(value: String?): Set<String> = value
	?.split('|')
	?.map(String::trim)
	?.filter(String::isNotEmpty)
	?.toSet()
	.orEmpty()

private fun Set<String>.serializeCollapsedGroups() = sorted().joinToString("|")

private fun getCurrentEntryAncestorPaths(currentURL: String): Set<String> {
	val currentEntry = docEntries.firstOrNull { it.path == currentURL } ?: return emptySet()
	return ancestorPathsOf(currentEntry.docSlugs).toSet()
}

private fun StyleScope.indentation(level: Int) = marginLeft(level * 1.5.cssRem)

private val docGroupOrder by lazy {
	AppGlobals["docGroupOrder"]
		?.split(',')
		?.map(String::trim)
		?.filter(String::isNotEmpty)
		.orEmpty()
}

private fun getGroupPriority(slug: String): Int {
	val index = docGroupOrder.indexOf(slug.lowercase())
	return if (index >= 0) index else docGroupOrder.size
}

/** Orders entries by explicit position, then configured group priority, then slug and nav title. */
private val docEntryComparator = Comparator<DocArticle> { a, b ->
	val slugsA = a.docSlugs
	val slugsB = b.docSlugs
	val maxDepth = minOf(slugsA.size, slugsB.size)

	for (i in 0 until maxDepth) {
		val slugA = slugsA[i]
		val slugB = slugsB[i]

		// A fixed position only applies to the leaf entry that declared it.
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

	compareValues(a.navTitle, b.navTitle)
}

private val orderedDocEntries by lazy { docEntries.sortedWith(docEntryComparator) }

/**
 * Returns the ordered list of DocArticle entries as they appear in the DocTree.
 * This is used by PageNavigation to determine previous/next pages.
 */
fun getOrderedDocEntries() = orderedDocEntries

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

/** Every group path that can be folded, i.e. every strict ancestor path of an entry. */
private val collapsiblePaths by lazy {
	docEntries.flatMapTo(mutableSetOf()) { ancestorPathsOf(it.docSlugs) }
}

/** The flattened tree rendered by [DocTree]. Doc entries are static, so it is built once. */
private val docNodes by lazy {
	val entryPaths = docEntries.mapTo(mutableSetOf()) { it.docSlugs.joinToString("/") }
	val processedGroups = mutableSetOf<String>()

	buildList {
		orderedDocEntries.forEach { entry ->
			val slugs = entry.docSlugs
			val entryPath = slugs.joinToString("/")

			ancestorPathsOf(slugs).forEachIndexed { index, groupPath ->
				if (!processedGroups.add(groupPath)) return@forEachIndexed
				if (groupPath in entryPaths) return@forEachIndexed
				add(DocNode.GroupNode(slugs[index].kebabCaseToTitleCamelCase(), index + 1, groupPath))
			}

			val isGroup = entryPath in collapsiblePaths
			add(DocNode.EntryNode(entry, slugs.size, isGroup, entryPath))

			if (isGroup) processedGroups.add(entryPath)
		}
	}
}

/**
 * The groups to fold for the page whose ancestors are [currentEntryAncestorPaths]: whatever the session remembers, or
 * every group on a first visit, minus the groups leading to the current page so it always stays visible.
 */
private fun resolveCollapsedGroups(currentEntryAncestorPaths: Set<String>): Set<String> {
	val stored = sessionStorage.getItem(DOC_TREE_COLLAPSED_GROUPS_KEY)
	val collapsed = if (stored == null) collapsiblePaths else parseCollapsedGroups(stored).intersect(collapsiblePaths)
	return collapsed - currentEntryAncestorPaths
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

	val currentURL = rememberPageContext().route.path
	val currentEntryAncestorPaths = remember(currentURL) { getCurrentEntryAncestorPaths(currentURL) }

	var collapsedGroups by remember { mutableStateOf(resolveCollapsedGroups(currentEntryAncestorPaths)) }
	var listElement by remember { mutableStateOf<HTMLUListElement?>(null) }

	LaunchedEffect(currentURL) {
		collapsedGroups = resolveCollapsedGroups(currentEntryAncestorPaths)
	}

	LaunchedEffect(collapsedGroups) {
		sessionStorage.setItem(DOC_TREE_COLLAPSED_GROUPS_KEY, collapsedGroups.serializeCollapsedGroups())
	}

	LaunchedEffect(listElement, currentURL) {
		val savedScroll = sessionStorage.getItem(DOC_TREE_SCROLL_KEY)?.toDoubleOrNull() ?: return@LaunchedEffect
		listElement?.scrollTop = savedScroll
	}

	DisposableEffect(listElement) {
		val element = listElement ?: return@DisposableEffect onDispose { }
		val listener: (Event) -> Unit = {
			sessionStorage.setItem(DOC_TREE_SCROLL_KEY, element.scrollTop.toString())
		}
		element.addEventListener("scroll", listener)
		onDispose { element.removeEventListener("scroll", listener) }
	}

	val visibleNodes = remember(collapsedGroups) {
		docNodes.filter { node -> collapsedGroups.none { node.groupPath.startsWith("$it/") } }
	}

	Div({
		id("doc-tree")
		classes(DocTreeStyle.container)
	}) {
		Div({
			classes(DocTreeStyle.header)
		}) {
			P({
				classes(DocTreeStyle.title)
			}) {
				Text("Pages")
			}

			Div({
				classes(DocTreeStyle.actions)
			}) {
				Button({
					classes(DocTreeStyle.actionButton)
					title("Unfold all groups")
					onClick { collapsedGroups = emptySet() }
				}) {
					Span({
						classes(DocTreeStyle.actionIcon)
					}) {
						MdiUnfoldMore()
					}
				}

				Button({
					classes(DocTreeStyle.actionButton)
					title("Fold all groups except current")
					onClick { collapsedGroups = collapsiblePaths - currentEntryAncestorPaths }
				}) {
					Span({
						classes(DocTreeStyle.actionIcon)
					}) {
						MdiUnfoldLess()
					}
				}
			}
		}

		Ul({
			classes(DocTreeStyle.list)
			ref {
				listElement = it
				onDispose { listElement = null }
			}
		}) {
			val onToggleCollapse: (String) -> Unit = { path ->
				collapsedGroups = if (path in collapsedGroups) collapsedGroups - path else collapsedGroups + path
			}

			visibleNodes.forEach { node ->
				node.Render(currentURL, collapsedGroups, onToggleCollapse)
			}
		}
	}
}

data object DocTreeStyle : StyleSheet() {
	val container by style {
		marginLeft(0.25.cssRem)

		smMax(self) {
			flex(1)
			minHeight(0.px)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			overflow(Overflow.Hidden)
		}
	}

	val title by style {
		fontSize(1.25.cssRem)
		fontWeight(FontWeight.Bold)
		marginY(0.6.cssRem)
		flexShrink(0)
	}

	val header by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.SpaceBetween)
		gap(0.5.cssRem)
	}

	val actions by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.15.cssRem)
		flexShrink(0)
	}

	val actionButton by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		fontSize(1.cssRem)
		padding(0.15.cssRem)
		opacity(0.7)
		transition(0.2.s, "color", "opacity")

		self + className("material-icons") style {
			fontSize(1.1.cssRem)
		}

		self + hover style {
			color(GlobalStyle.linkColor)
			opacity(1)
		}
	}

	val actionIcon by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)

		child(self, type("svg")) style {
			width(1.05.cssRem)
			height(1.05.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val list by style {
		listStyle(ListStyle.None)
		left(0.px)
		marginY(0.2.cssRem)
		maxHeight(75.vh)
		overflowY(Overflow.Auto)
		padding(0.4.cssRem)
		position(Position.Sticky)

		smMax(self) {
			marginBottom(0.px)
			maxHeight(MaxHeight.Unset)
		}
	}

	val entry by style {
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Block)
		fontSize(0.95.cssRem)
		padding(0.15.cssRem, 0.3.cssRem)
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
		width(0.9.cssRem)
		height(0.9.cssRem)
		transition(0.2.s, "transform")
		transform { rotate(90.deg) }
		userSelect(UserSelect.None)

		self + className("material-icons") style {
			fontSize(1.1.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val collapsed by style {
		transform { rotate(0.deg) }
	}

	val topLevelGroup by style {
		marginTop(0.5.cssRem)
	}
}
