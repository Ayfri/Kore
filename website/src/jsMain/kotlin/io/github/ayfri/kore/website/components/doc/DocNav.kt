package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.docSections
import kotlin.js.Date

private const val NEW_PAGE_DAYS = 14

data class DocArticle(
	val date: String,
	val dateModified: String,
	val desc: String,
	val keywords: List<String>,
	val navTitle: String,
	val path: String,
	val title: String,
) {
	/** Read on the visitor's clock, so the tag goes away [NEW_PAGE_DAYS] days after `date-created` without a deploy. */
	val isNew get() = Date.now() - Date(date).getTime() < NEW_PAGE_DAYS * 86_400_000.0

	/** The extra module an `oop` or `helpers` page needs, those pages keeping their module folder in the route. */
	val module get() = path.split('/').getOrNull(2)?.takeIf { it == "helpers" || it == "oop" }
}

/**
 * [description], [sprite] and [title] are null for the single group of a flat section.
 * [sprite] is a vanilla texture path for `McSprite`, like `block/command_block_front`.
 */
data class DocGroup(val description: String?, val pages: List<DocArticle>, val sprite: String?, val title: String?)

data class DocSection(val groups: List<DocGroup>, val icon: @Composable () -> Unit, val id: String, val title: String) {
	val firstPage get() = groups.first().pages.first()
	val isGrouped get() = groups.first().title != null
}

/** Where a page sits in the nav, [group] being null in a flat section. */
data class DocLocation(val group: DocGroup?, val section: DocSection)

private val docLocations by lazy {
	docSections.flatMap { section ->
		section.groups.flatMap { group ->
			group.pages.map { it.path to DocLocation(group.takeIf { section.isGrouped }, section) }
		}
	}.toMap()
}

fun docLocation(path: String) = docLocations[path]
