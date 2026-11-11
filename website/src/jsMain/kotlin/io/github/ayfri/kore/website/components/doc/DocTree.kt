package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.kore.website.docSections
import io.github.ayfri.kore.website.utils.onEvents
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.events.Event

private const val DOC_TREE_COLLAPSED_KEY = "doc-tree:collapsed"
private const val DOC_TREE_SCROLL_KEY = "doc-tree:scroll-top"

private fun DocSection.keyOf(group: DocGroup) = "$id/${group.title}"

/** The keys of the groups a grouped section can fold, none for a flat section. */
private val DocSection.groupKeys get() = if (isGrouped) groups.mapTo(mutableSetOf()) { keyOf(it) } else emptySet<String>()

private val allGroupKeys by lazy { docSections.flatMapTo(mutableSetOf()) { it.groupKeys } }

/**
 * The groups to fold: whatever the session remembers, or every group on a first visit, minus [currentKey] so the group
 * holding the current page stays open.
 */
private fun resolveCollapsedGroups(currentKey: String?): Set<String> {
	val stored = sessionStorage.getItem(DOC_TREE_COLLAPSED_KEY)
	val collapsed = if (stored == null) allGroupKeys else stored.split('\n').filter(String::isNotEmpty).toSet().intersect(allGroupKeys)
	return collapsed - setOfNotNull(currentKey)
}

@Composable
private fun GroupRow(group: DocGroup, collapsed: Boolean, holdsCurrent: Boolean, onToggle: () -> Unit) = Button({
	classes(DocTreeStyle.groupRow)
	if (holdsCurrent) classes(DocTreeStyle.groupRowCurrent)
	attr("aria-expanded", (!collapsed).toString())
	onClick { onToggle() }
}) {
	Span({
		classes(DocTreeStyle.collapseToggle)
		if (collapsed) classes(DocTreeStyle.collapsed)
	}) {
		LucideChevronRight()
	}
	Span {
		Text(group.title.orEmpty())
	}
	if (collapsed && group.pages.any { it.isNew }) NewTag()
}

@Composable
private fun NewTag() = Span({
	classes(DocTreeStyle.newTag)
}) {
	Text("New")
}

@Composable
private fun PageRow(article: DocArticle, selected: Boolean) = Li {
	A(article.path, {
		classes(DocTreeStyle.pageRow)
		title(article.navTitle)
		if (selected) {
			classes(DocTreeStyle.selected)
			attr("aria-current", "page")
		}
	}) {
		Span({
			classes(DocTreeStyle.pageTitle)
		}) {
			Text(article.navTitle)
		}
		if (article.isNew) NewTag()
		article.module?.let { module ->
			Span({
				classes(DocTreeStyle.moduleTag)
				title("Needs the $module module")
			}) {
				Text(module)
			}
		}
	}
}

@Composable
fun DocTree(section: DocSection, currentPath: String) {
	val currentKey = docLocation(currentPath)?.group?.let { section.keyOf(it) }
	val scrollKey = "$DOC_TREE_SCROLL_KEY:${section.id}"
	val sectionKeys = remember(section.id) { section.groupKeys }

	var collapsedGroups by remember { mutableStateOf(resolveCollapsedGroups(currentKey)) }
	var listElement by remember { mutableStateOf<HTMLUListElement?>(null) }

	LaunchedEffect(currentPath) {
		collapsedGroups = resolveCollapsedGroups(currentKey)
	}

	LaunchedEffect(collapsedGroups) {
		sessionStorage.setItem(DOC_TREE_COLLAPSED_KEY, collapsedGroups.sorted().joinToString("\n"))
	}

	LaunchedEffect(listElement, currentPath) {
		val savedScroll = sessionStorage.getItem(scrollKey)?.toDoubleOrNull() ?: return@LaunchedEffect
		listElement?.scrollTop = savedScroll
	}

	/** Every doc link is a full document load, so the scroll position only needs saving when the page goes away. */
	window.onEvents(
		"pagehide" to { _: Event -> listElement?.let { sessionStorage.setItem(scrollKey, it.scrollTop.toString()) } },
		key = listElement,
	)

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
				Text(section.title)
			}

			if (section.isGrouped) {
				Div({
					classes(DocTreeStyle.actions)
				}) {
					Button({
						classes(DocTreeStyle.actionButton)
						title("Unfold all groups")
						onClick { collapsedGroups = collapsedGroups - sectionKeys }
					}) {
						Span({
							classes(DocTreeStyle.actionIcon)
						}) {
							LucideChevronsUpDown()
						}
					}

					Button({
						classes(DocTreeStyle.actionButton)
						title("Fold all groups except current")
						onClick { collapsedGroups = collapsedGroups + sectionKeys - setOfNotNull(currentKey) }
					}) {
						Span({
							classes(DocTreeStyle.actionIcon)
						}) {
							LucideChevronsDownUp()
						}
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
			if (section.isGrouped) {
				section.groups.forEach { group ->
					val key = section.keyOf(group)
					val collapsed = key in collapsedGroups

					Li {
						GroupRow(group, collapsed, key == currentKey) {
							collapsedGroups = if (collapsed) collapsedGroups - key else collapsedGroups + key
						}

						if (!collapsed) {
							Ul({
								classes(DocTreeStyle.nestedList)
							}) {
								group.pages.forEach { PageRow(it, it.path == currentPath) }
							}
						}
					}
				}
			} else {
				section.groups.first().pages.forEach { PageRow(it, it.path == currentPath) }
			}
		}
	}
}
