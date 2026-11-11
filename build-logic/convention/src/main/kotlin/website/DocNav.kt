package website

import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import java.io.File

private val iconRegex = Regex("[A-Z][A-Za-z0-9]*")
private val spriteRegex = Regex("(block|item)/[a-z0-9_]+")

/** A string as a Kotlin literal, quotes included, with the characters the compiler reads inside a string escaped. */
internal fun String.kotlinString() =
	"\"" + replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$").replace("\n", "\\n").replace("\r", "\\r") + "\""

/**
 * A titled run of pages in a grouped section, or the whole page list of a flat section when [title] is null.
 * [sprite] is the vanilla texture drawn on the group's card, relative to `textures/` without extension.
 */
data class DocNavGroup(val description: String?, val routes: List<String>, val sprite: String?, val title: String?)

data class DocNavSection(val groups: List<DocNavGroup>, val icon: String, val id: String, val title: String) {
	val grouped get() = groups.first().title != null
}

/** The doc structure and page order of `markdown/doc/nav.json`, the single source of the sidebar, breadcrumbs, `llms.txt` and the prev/next links. */
class DocNav(val sections: List<DocNavSection>) {
	val routes = sections.flatMap { section -> section.groups.flatMap { it.routes } }

	/** Fails the build, listing every difference, when the nav and the pages declare different routes. */
	fun check(entries: List<DocEntry>) {
		val navRoutes = routes.toSet()
		val pageRoutes = entries.mapTo(mutableSetOf()) { it.path }
		val errors = routes.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.map { "nav.json lists $it twice" } +
			entries.filter { it.path !in navRoutes }.map { "${it.filePath} (${it.path}) is missing from nav.json" } +
			navRoutes.filter { it !in pageRoutes }.map { "nav.json lists $it, which no page declares in its routeOverride" }
		if (errors.isNotEmpty()) {
			throw GradleException(errors.joinToString("\n", "The doc nav doesn't match the pages:\n"))
		}
	}

	/** "Reference / Items & loot", or the section title alone for a flat section. Used for the OG card label. */
	fun label(route: String): String {
		sections.forEach { section ->
			val group = section.groups.firstOrNull { route in it.routes } ?: return@forEach
			return if (section.grouped) "${section.title} / ${group.title}" else section.title
		}
		throw GradleException("nav.json doesn't list $route")
	}

	/** The `docSections` tree and the `docEntries` list of the site, [entries] being the pages by path. */
	fun kotlinSource(entries: Map<String, DocEntry>, sitePackage: String) = buildString {
		fun line(depth: Int, text: String) = appendLine("\t".repeat(depth) + text)

		appendLine("// This file is generated. Do not modify directly.")
		appendLine()
		appendLine("package $sitePackage")
		appendLine()
		appendLine("import $sitePackage.components.doc.DocArticle")
		appendLine("import $sitePackage.components.doc.DocGroup")
		appendLine("import $sitePackage.components.doc.DocSection")
		appendLine()
		appendLine("val docSections = listOf(")
		sections.forEach { section ->
			line(1, "DocSection(")
			line(2, "groups = listOf(")
			section.groups.forEach { group ->
				line(3, "DocGroup(")
				line(4, "description = ${group.description?.kotlinString()},")
				line(4, "pages = listOf(")
				group.routes.forEach { route ->
					val entry = entries.getValue(route)
					line(5, "DocArticle(")
					line(6, "date = ${entry.date.kotlinString()},")
					line(6, "dateModified = ${entry.dateModified.kotlinString()},")
					line(6, "desc = ${entry.desc.kotlinString()},")
					line(6, "keywords = listOf(${entry.keywords.joinToString { it.kotlinString() }}),")
					line(6, "navTitle = ${entry.navTitle.kotlinString()},")
					line(6, "path = ${entry.path.kotlinString()},")
					line(6, "title = ${entry.title.kotlinString()},")
					line(5, "),")
				}
				line(4, "),")
				line(4, "sprite = ${group.sprite?.kotlinString()},")
				line(4, "title = ${group.title?.kotlinString()},")
				line(3, "),")
			}
			line(2, "),")
			line(2, "icon = { com.varabyte.kobweb.silk.components.icons.lucide.Lucide${section.icon}() },")
			line(2, "id = ${section.id.kotlinString()},")
			line(2, "title = ${section.title.kotlinString()},")
			line(1, "),")
		}
		appendLine(")")
		appendLine()
		appendLine("/** Every doc page in nav order, the order of the sidebar, prev/next and the page transitions. */")
		appendLine("val docEntries = docSections.flatMap { section -> section.groups.flatMap { it.pages } }")
	}

	companion object {
		fun read(file: File): DocNav {
			val root = try {
				JsonSlurper().parseText(file.readText())
			} catch (exception: JsonException) {
				throw GradleException("$file isn't valid JSON: ${exception.message}", exception)
			}
			val sections = (root as? Map<*, *>)?.get("sections") as? List<*>
			if (sections.isNullOrEmpty()) throw GradleException("nav.json needs a non-empty 'sections' list")

			val ids = mutableSetOf<String>()
			return DocNav(sections.mapIndexed { index, section ->
				val where = "section ${index + 1}"
				val map = section as? Map<*, *> ?: throw GradleException("nav.json: $where isn't an object")
				val id = map.text("id", where)
				if (!ids.add(id)) throw GradleException("nav.json: $where repeats the section id '$id'")
				val pages = map["pages"]
				val groups = map["groups"]
				val navGroups = when {
					(pages == null) == (groups == null) -> throw GradleException("nav.json: $where needs either 'pages' or 'groups'")
					pages != null -> listOf(DocNavGroup(description = null, routes = routes(pages, where), sprite = null, title = null))
					else -> {
						val list = (groups as? List<*>)?.takeIf { it.isNotEmpty() } ?: throw GradleException("nav.json: $where needs a non-empty 'groups' list")
						val titles = mutableSetOf<String>()
						list.mapIndexed { groupIndex, group ->
							val groupWhere = "group ${groupIndex + 1} of $where"
							val groupMap = group as? Map<*, *> ?: throw GradleException("nav.json: $groupWhere isn't an object")
							val title = groupMap.text("title", groupWhere)
							if (!titles.add(title)) throw GradleException("nav.json: $where has two groups titled '$title'")
							DocNavGroup(
								description = groupMap.text("description", "group '$title'"),
								routes = routes(groupMap["pages"], "group '$title'"),
								sprite = groupMap.sprite("group '$title'"),
								title = title,
							)
						}
					}
				}
				DocNavSection(groups = navGroups, icon = map.icon(where), id = id, title = map.text("title", where))
			})
		}

		private fun Map<*, *>.icon(where: String) = text("icon", where).also {
			if (!iconRegex.matches(it)) throw GradleException("nav.json: $where has the icon '$it', which isn't a Lucide name like 'BookOpen'")
		}

		private fun Map<*, *>.sprite(where: String) = text("sprite", where).also {
			if (!spriteRegex.matches(it)) throw GradleException("nav.json: $where has the sprite '$it', which isn't a texture path like 'block/command_block_front'")
		}

		private fun Map<*, *>.text(key: String, where: String) =
			(this[key] as? String)?.takeIf { it.isNotBlank() } ?: throw GradleException("nav.json: $where misses '$key'")

		private fun routes(value: Any?, where: String): List<String> {
			val list = (value as? List<*>)?.takeIf { it.isNotEmpty() } ?: throw GradleException("nav.json: $where needs a non-empty 'pages' list")
			return list.map { route ->
				(route as? String)?.takeIf { it.startsWith("/docs/") } ?: throw GradleException("nav.json: $where lists $route, which isn't a '/docs/...' route")
			}
		}
	}
}
