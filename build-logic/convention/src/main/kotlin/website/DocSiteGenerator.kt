package website

import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock.ProcessScope
import com.varabyte.kobwebx.gradle.markdown.MarkdownEntry
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import java.io.File

/** Also available to the site as `AppGlobals["websiteUrl"]`. */
private const val BASE_URL = Project.WEBSITE_URL
private const val GITHUB_URL = "https://${Project.GITHUB_URL}"
private const val MAVEN_KORE = "${Project.GROUP}:kore"
private const val SITE_PACKAGE = "${Project.GROUP}.website"
private const val SUMMARY = "Kore is an open-source Kotlin DSL and library that generates type-safe Minecraft Java Edition datapacks: functions, commands, loot tables, recipes, advancements, worldgen and more are written as Kotlin code instead of JSON and MCFunction by hand."

private val blankRunRegex = Regex("""\n{3,}""")

/** Also catches the misspellings `docConstantRegex` skips, `{{ kore.release }}` or `{{minecraft.version}}`. */
private val docConstantLikeRegex = Regex("""\{\{\s*(?:java|kore|mc|minecraft)\b[^}]*}}""")

/** Doc placeholders like `{{kore.release}}`, listed in the "Documentation pages" section of `contributing/Architecture_and_Patterns.md`. */
private val docConstantRegex = Regex("""\{\{((?:java|kore|mc)\.\w+)}}""")
private val docLinkRegex = Regex("""]\(/(docs/[^)#\s]*)(#[^)\s]*)?\)""")
private val fenceRegex = Regex("""(?ms)^[ \t]*(?<fence>`{3,}|~{3,}).*?^[ \t]*\k<fence>[`~]*[ \t]*$""")
private val frontMatterRegex = Regex("""\A---\r?\n.*?\r?\n---\r?\n""", RegexOption.DOT_MATCHES_ALL)
private val headingRegex = Regex("""(?m)^#{1,6} .*""")
private val rootRelativeRegex = Regex("""]\(/(?!/)""")
private val spriteRegex = Regex("""(?<!`)!\[[^\]]*]\(mc:[^)]*\) ?""")
private val widgetRegex = Regex("""(?m)^\{\{\{\s*([^\s}]+)\s*\}\}\}[ \t]*\r?\n?""")

private val groupLabels = mapOf("data-driven" to "Data-driven", "getting-started" to "Getting started", "oop" to "OOP")
private val requiredFields = listOf("date-created", "date-modified", "description", "keywords", "nav-title", "root", "routeOverride", "title")

/**
 * Runs inside the `process` callback of Kobweb's markdown plugin: reads the front matter of every doc page, then writes `docEntries.kt`,
 * `docWidgets.kt`, the public resources (`doc-data/<route>.json` with the content of each page, `llms.txt`, `llms-full.txt`, `sitemap.xml`,
 * `markdown-sources.json` and one `.md` copy per page) and, when [renderOgImages] is set, the Open Graph cards.
 * Only holds plain values so the configuration cache can serialize it.
 */
class DocSiteGenerator(
	private val docGroupOrder: List<String>,
	private val featureCards: File,
	private val gitHubDataDir: File,
	private val logo: File,
	private val markdownDir: File,
	private val minecraftVersion: String,
	private val ogDir: File,
	private val ogFontsDir: File,
	private val renderOgImages: Boolean,
) {
	fun generate(scope: ProcessScope, pages: List<MarkdownEntry>) {
		val docEntries = pages.map { it.toDocEntry() }
		val sortedEntries = docEntries.sortedWith(entryOrder)
		val releases = ReleaseHistory(readReleases())

		/** The dev server falls back on `minecraft.version`, the deployed site must never show a snapshot as the latest stable release. */
		if (releases.latestStable == null && renderOgImages) {
			throw GradleException("No stable GitHub release in releases.tsv, the doc placeholders would show Minecraft $minecraftVersion as the latest stable version: rerun `fetchGitHubReleases --rerun`.")
		}
		val docConstants = docConstants(releases)

		val widgets = mapOf("FeatureGrid" to featureGrid(), "VersionMatrix" to versionMatrix(releases))
		val routes = sortedEntries.map { it.route }.toSet()
		val markdownSources = pages.associate { it.filePath to markdownDir.resolve(it.filePath).readText() }
		checkDocConstants(markdownSources, docConstants)
		val expandedSources = markdownSources.mapValues { it.value.expandDocConstants(docConstants) }

		val parser = docParser()
		val widgetCalls = sortedSetOf<String>()
		sortedEntries.forEach { entry ->
			/** The front matter becomes blank lines, so the line numbers of the parsed nodes stay the lines of the file. */
			val source = expandedSources.getValue(entry.filePath).replace(frontMatterRegex) { "\n".repeat(it.value.count { char -> char == '\n' }) }
			scope.generatePublicResource("doc-data/${entry.route}.json", DocPageJson(entry.filePath, widgetCalls).write(parser.parse(source)))
		}
		scope.generateKotlin("$SITE_PACKAGE/docWidgets.kt", docWidgetsSource(widgetCalls))

		val groupedEntries = sortedEntries.groupBy { it.slugs.getOrNull(1) ?: "other" }
		/** Single-page sections outside `docGroupOrder` (home, getting started) come first. */
		val orderedGroups = groupedEntries.keys.sortedBy { if (it in docGroupOrder) groupPriority(it) else -1 }
		val pageMarkdown = orderedGroups.flatMap { groupedEntries.getValue(it) }.associateWith { it.toMarkdown(expandedSources.getValue(it.filePath), widgets, routes) }

		scope.generatePublicResource("llms.txt", llmsIndex(groupedEntries, orderedGroups, releases))
		/** The same pages as the `.md` copies, concatenated in the `llms.txt` order. */
		scope.generatePublicResource("llms-full.txt", "# Kore documentation\n\n> $SUMMARY\n\n" + pageMarkdown.values.joinToString("\n\n---\n\n") + "\n")
		pageMarkdown.forEach { (entry, markdown) -> scope.generatePublicResource("${entry.route}.md", markdown + "\n") }
		scope.generatePublicResource("markdown-sources.json", JsonOutput.toJson(expandedSources))
		scope.generatePublicResource("sitemap.xml", sitemap(sortedEntries))
		if (renderOgImages) renderOgCards(sortedEntries)

		scope.generateKotlin("$SITE_PACKAGE/docEntries.kt", docEntriesSource(docEntries))
	}

	private fun MarkdownEntry.toDocEntry(): DocEntry {
		val fields = requiredFields.associateWith { field ->
			frontMatter[field]?.firstOrNull()
				?: throw GradleException("'$filePath' misses required fields in its front matter: ${requiredFields.filter { frontMatter[it] == null }}")
		}
		return DocEntry(
			date = fields.getValue("date-created").toIsoDate(),
			dateModified = fields.getValue("date-modified").toIsoDate(),
			desc = fields.getValue("description"),
			filePath = filePath,
			keywords = fields.getValue("keywords").split(Regex(""",\s*""")),
			navTitle = fields.getValue("nav-title"),
			position = frontMatter["position"]?.firstOrNull()?.toIntOrNull(),
			slugs = fields.getValue("routeOverride").split("/").drop(1),
			title = fields.getValue("title"),
		)
	}

	/** `2025-3-7` -> `2025-03-07T00:00:00Z`, the ISO 8601 form of the dates. */
	private fun String.toIsoDate() = split("-").let { (year, month, day) ->
		"$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}T00:00:00Z"
	}

	private fun groupPriority(slug: String) = docGroupOrder.indexOf(slug.lowercase()).takeIf { it >= 0 } ?: docGroupOrder.size

	/** Sorts entries by group priority, then optional position, then slug/title names. */
	private val entryOrder = Comparator<DocEntry> { a, b ->
		val slugsA = a.slugs.drop(1)
		val slugsB = b.slugs.drop(1)
		val maxDepth = minOf(slugsA.size, slugsB.size)

		for (i in 0 until maxDepth) {
			val slugA = slugsA[i]
			val slugB = slugsB[i]

			/** A fixed position only applies to leaf entries, where it is set explicitly. */
			val posA = if (i == slugsA.lastIndex) a.position else null
			val posB = if (i == slugsB.lastIndex) b.position else null

			val posCompare = compareValues(posA ?: Int.MAX_VALUE, posB ?: Int.MAX_VALUE)
			if (posCompare != 0) return@Comparator posCompare

			if (i == 0 && slugsA.size > 1 && slugsB.size > 1) {
				val groupCompare = compareValues(groupPriority(slugA), groupPriority(slugB))
				if (groupCompare != 0) return@Comparator groupCompare
			}

			val slugCompare = compareValues(slugA.lowercase(), slugB.lowercase())
			if (slugCompare != 0) return@Comparator slugCompare
		}

		val lengthCompare = compareValues(slugsA.size, slugsB.size)
		if (lengthCompare != 0) return@Comparator lengthCompare

		return@Comparator compareValues(a.navTitle, b.navTitle)
	}

	private fun readReleases() = gitHubDataDir.resolve("releases.tsv").takeIf { it.exists() }?.readLines().orEmpty()
		.filter { it.isNotBlank() }
		.map { line -> line.split('\t').let { (tag, prerelease, published) -> ReleaseEntry(prerelease = prerelease.toBoolean(), published = published, tag = tag) } }
		.filter { it.minecraftVersion.isNotEmpty() }
		.sortedByDescending { it.published }

	private fun docConstants(releases: ReleaseHistory): Map<String, String> {
		val latestStable = releases.latestStable
		val stableMinecraftVersion = latestStable?.minecraftVersion ?: minecraftVersion
		val versionsFile = gitHubDataDir.resolve("versions_data.json")
		val versionsData = versionsFile.takeIf { it.exists() }?.let { JsonSlurper().parseText(it.readText()) as Map<*, *> }.orEmpty()
		fun Map<*, *>?.field(key: String) = this?.get(key)?.toString()
			?: throw GradleException("No `$key` for Minecraft $stableMinecraftVersion in $versionsFile, rerun `fetchMinecraftVersions --rerun`.")
		val stableVersionData = versionsData[stableMinecraftVersion] as? Map<*, *>
		/** A plain integer before 1.21.9, a `{major, minor}` object since. */
		val dataPackFormat = stableVersionData?.get("data_pack")?.let { it as? Map<*, *> ?: mapOf("major" to it, "minor" to 0) }
		return mapOf(
			"java.version" to stableVersionData.field("java"),
			"kore.mavenSnapshot" to "${Project.VERSION}-$minecraftVersion-SNAPSHOT",
			"kore.release" to (latestStable?.coordinate ?: "${Project.VERSION}-$minecraftVersion"),
			"kore.snapshot" to ((releases.latestPreRelease ?: latestStable)?.coordinate ?: "${Project.VERSION}-$minecraftVersion"),
			"kore.version" to (latestStable?.koreVersion ?: Project.VERSION),
			"mc.packFormatMajor" to dataPackFormat.field("major"),
			"mc.packFormatMinor" to dataPackFormat.field("minor"),
			"mc.version" to stableMinecraftVersion,
		)
	}

	private fun docWidgetsSource(calls: Set<String>) = buildString {
		appendLine("// This file is generated. Do not modify directly.")
		appendLine()
		appendLine("package $SITE_PACKAGE")
		appendLine()
		appendLine("import androidx.compose.runtime.Composable")
		appendLine()
		appendLine("/** The `{{{ }}}` widgets of the doc pages, keyed on the call as written in the Markdown. */")
		appendLine("@Composable")
		appendLine("fun DocWidgetCall(call: String) {")
		appendLine("\twhen (call) {")
		calls.forEach { appendLine("\t\t\"${it.replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$")}\" -> ${KobwebCall(it).toFqn(SITE_PACKAGE)}") }
		appendLine("\t\telse -> error(\"No doc widget \$call\")")
		appendLine("\t}")
		appendLine("}")
	}

	private fun versionMatrix(releases: ReleaseHistory) = if (releases.entries.isEmpty()) "Every version is listed at $BASE_URL/updates.\n" else buildString {
		appendLine("| Minecraft version | Kore version | Type | Gradle coordinate | Published |")
		appendLine("| --- | --- | --- | --- | --- |")
		appendLine("| $minecraftVersion | ${Project.VERSION} | Maven Snapshot | `$MAVEN_KORE:${Project.VERSION}-$minecraftVersion-SNAPSHOT` | master (continuous) |")
		releases.latestPreRelease?.let { appendLine("| ${it.minecraftVersion} | ${it.koreVersion} | Snapshot | `$MAVEN_KORE:${it.coordinate}` | ${it.published.take(10)} |") }
		releases.stableRows.forEach { appendLine("| ${it.minecraftVersion} | ${it.koreVersion} | Stable | `$MAVEN_KORE:${it.coordinate}` | ${it.published.take(10)} |") }
	}

	/** The home page topic cards, read from the widget source so the links can't drift from it. */
	private fun featureGrid() = featureCards.readText()
		.substringAfter("fun FeatureGrid()")
		.split("FeatureCard(").drop(1)
		.mapNotNull { card ->
			val title = Regex("""title = "([^"]*)"""").find(card)?.groupValues?.get(1) ?: return@mapNotNull null
			val links = Regex("""FeatureLink\("([^"]*)", "([^"]*)"\)""").findAll(card).joinToString(", ") { "[${it.groupValues[2]}](${it.groupValues[1]})" }
			"- **$title**: $links"
		}.joinToString("\n", postfix = "\n")

	private fun checkDocConstants(markdownSources: Map<String, String>, docConstants: Map<String, String>) {
		val errors = markdownSources.flatMap { (path, source) ->
			val frontMatter = frontMatterRegex.find(source)?.value.orEmpty()
			val headings = headingRegex.findAll(source.replace(fenceRegex, "")).joinToString("\n") { it.value }
			docConstantLikeRegex.findAll(source)
				.filter { docConstantRegex.matchEntire(it.value)?.groupValues?.get(1) !in docConstants }
				.map { "$path: unknown placeholder ${it.value}" }.toList() +
				listOfNotNull(
					docConstantRegex.find(frontMatter)?.let { "$path: ${it.value} in the frontmatter, which isn't expanded" },
					docConstantRegex.find(headings)?.let { "$path: ${it.value} in a heading, whose anchor would change on every release" },
				)
		}
		if (errors.isNotEmpty()) {
			throw GradleException(errors.joinToString("\n", "Invalid doc placeholders, the known ones are ${docConstants.keys}:\n"))
		}
	}

	private fun String.expandDocConstants(docConstants: Map<String, String>) = replace(docConstantRegex) { docConstants.getValue(it.groupValues[1]) }

	/** Outside code fences: widgets become Markdown (mockups vanish), `mc:` sprites go, root-relative links turn absolute. */
	private fun String.toPlainMarkdown(widgets: Map<String, String>, routes: Set<String>): String {
		val fences = fenceRegex.findAll(this).map { it.value }.toList()
		val prose = fenceRegex.split(this).map { chunk ->
			chunk
				.replace(widgetRegex) { widgets[it.groupValues[1].substringAfterLast('.')].orEmpty() }
				.replace(spriteRegex, "")
				.replace(docLinkRegex) { if (it.groupValues[1] in routes) "]($BASE_URL/${it.groupValues[1]}.md${it.groupValues[2]})" else it.value }
				.replace(rootRelativeRegex, "]($BASE_URL/")
				.replace(blankRunRegex, "\n\n")
		}
		return prose.indices.joinToString("") { prose[it] + fences.getOrElse(it) { "" } }
	}

	/** The page as clean Markdown: its own H1 (the frontmatter title when it has none), the description, the update date and the canonical URL, then the body. */
	private fun DocEntry.toMarkdown(expandedSource: String, widgets: Map<String, String>, routes: Set<String>): String {
		val body = expandedSource.replace(frontMatterRegex, "").toPlainMarkdown(widgets, routes).trim()
		val ownHeading = body.startsWith("# ")
		return buildString {
			appendLine("# ${if (ownHeading) body.substringBefore('\n').removePrefix("# ") else title}")
			appendLine()
			appendLine("> $desc")
			appendLine()
			appendLine("Updated ${dateModified.take(10)}. Canonical: $BASE_URL/$route")
			appendLine()
			append(if (ownHeading) body.substringAfter('\n', "").trimStart() else body)
		}
	}

	/** The llmstxt.org index, every page linking to its Markdown copy. */
	private fun llmsIndex(groupedEntries: Map<String, List<DocEntry>>, orderedGroups: List<String>, releases: ReleaseHistory): String {
		val latestVersions = releases.latestStable?.let { stable ->
			val preRelease = releases.latestPreRelease?.let { ", the latest pre-release is `$MAVEN_KORE:${it.coordinate}` for Minecraft ${it.minecraftVersion}" }.orEmpty()
			"The latest stable release is `$MAVEN_KORE:${stable.coordinate}` for Minecraft ${stable.minecraftVersion}$preRelease."
		} ?: "The current versions are listed at $BASE_URL/updates."

		return buildString {
			appendLine("# Kore")
			appendLine()
			appendLine("> $SUMMARY")
			appendLine()
			appendLine("Kore is published on Maven Central under the group `io.github.ayfri.kore` (artifacts `kore`, `oop`, `helpers` and `bindings`, all Kotlin Multiplatform for the JVM and JavaScript). $latestVersions Releases are versioned `<kore version>-<minecraft version>`, a Gradle project adds one with `implementation(\"$MAVEN_KORE:<version>\")` and the Getting Started page covers the setup. Source code, issues and releases: $GITHUB_URL")
			appendLine()
			appendLine("Every documentation page below links to its Markdown copy, the same URL without the `.md` suffix is the HTML page.")
			appendLine()

			orderedGroups.forEach { group ->
				appendLine("## ${groupLabels[group] ?: group.replaceFirstChar { it.uppercase() }}")
				appendLine()

				groupedEntries[group]?.forEach { entry ->
					append("- [${entry.title}]($BASE_URL/${entry.route}.md)")
					if (entry.desc.isNotBlank()) append(": ${entry.desc}")
					appendLine()
				}
				appendLine()
			}

			appendLine("## Optional")
			appendLine()
			appendLine("- [Full documentation]($BASE_URL/llms-full.txt): every page in one file")
			appendLine("- [Playground]($BASE_URL/playground): write and run Kore code in the browser")
			appendLine("- [Releases]($BASE_URL/updates): changelog of every Kore version")
			appendLine("- [GitHub repository]($GITHUB_URL): source code, issues and releases")
			appendLine("- [Kore Template](https://github.com/Kore-Minecraft/Kore-Template): ready-to-run starter project")
			appendLine("- [Kore Skill](https://github.com/Kore-Minecraft/Kore-Skill): agent skill for writing Kore code")
			appendLine("- [Discord](https://discord.ayfri.com): community support")
		}
	}

	private fun sitemap(sortedEntries: List<DocEntry>) = buildString {
		fun url(path: String, priority: String, changefreq: String = "weekly", lastmod: String? = null) {
			appendLine("""	<url>""")
			appendLine("""		<loc>$BASE_URL/$path</loc>""")
			lastmod?.let { appendLine("""		<lastmod>$it</lastmod>""") }
			appendLine("""		<changefreq>$changefreq</changefreq>""")
			appendLine("""		<priority>$priority</priority>""")
			appendLine("""	</url>""")
		}
		appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
		appendLine("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")
		url("", "1.0")
		url("features", "0.9")
		url("updates", "0.9")
		sortedEntries.forEach { entry ->
			val priority = when (entry.slugs.size) {
				in 0..2 -> "0.9"
				3 -> "0.7"
				else -> "0.5"
			}
			url(entry.route, priority, "monthly", entry.dateModified.take(10))
		}
		appendLine("</urlset>")
	}

	/** Open Graph cards, `/og/<route>.png` per doc page, `/og/features.png`, `/og/updates.png` and `/og/default.png` for the other pages. */
	private fun renderOgCards(sortedEntries: List<DocEntry>) {
		val host = BASE_URL.substringAfter("://")
		val stars = gitHubDataDir.resolve("stars.txt").takeIf { it.exists() }?.readText()?.toIntOrNull()
		val ogRenderer = OgImageRenderer(ogFontsDir, logo, stars)
		ogRenderer.render(
			ogDir.resolve("default.png"),
			label = "Open-source Kotlin DSL",
			title = "Type-safe Minecraft datapacks, written in Kotlin",
			description = "Create datapacks without writing JSON or MCFunction by hand.",
			footer = "$host  ·  Kore ${Project.VERSION} for Minecraft $minecraftVersion",
		)
		ogRenderer.render(
			ogDir.resolve("features.png"),
			label = "Features",
			title = "Commands, JSON resources, worldgen & tooling",
			description = "Typed commands, loot tables, recipes, worldgen, gameplay helpers, Gradle plugin and mod jar export.",
			footer = "$host/features",
		)
		val latestTag = gitHubDataDir.resolve("latest-release-tag.txt").takeIf { it.exists() }?.readText()?.removePrefix("v").orEmpty()
		ogRenderer.render(
			ogDir.resolve("updates.png"),
			label = "Latest release",
			title = latestTag.split("-", limit = 2).let { parts ->
				when {
					latestTag.isEmpty() -> "Kore releases"
					parts.size == 2 -> "Kore ${parts[0]} for Minecraft ${parts[1]}"
					else -> "Kore $latestTag"
				}
			},
			description = "Changelog and release history of every Kore version, fetched from GitHub.",
			footer = "$host/updates",
		)
		sortedEntries.parallelStream().forEach { entry ->
			ogRenderer.render(
				ogDir.resolve("${entry.route}.png"),
				label = entry.slugs.dropLast(1).joinToString(" / ") { it.replace("-", " ") },
				title = entry.navTitle,
				description = entry.desc,
				footer = "$host/${entry.route}",
			)
		}
	}

	private fun docEntriesSource(docEntries: List<DocEntry>) = buildString {
		appendLine(
			"""
			|// This file is generated. Do not modify directly.
			|
			|package $SITE_PACKAGE
			|
			|import $SITE_PACKAGE.components.doc.DocArticle
			|
			|val docEntries = listOf${if (docEntries.isEmpty()) "<DocArticle>" else ""}(
			""".trimMargin()
		)

		fun List<String>.asCode() = "listOf(${joinToString { "\"$it\"" }})"
		fun String.escapeQuotes() = replace("\"", "\\\"")

		docEntries.sortedWith(
			compareBy(
				{ it.slugs.firstOrNull() ?: "" },
				{ it.position ?: Int.MAX_VALUE },
				{ it.navTitle })
		).forEach { entry ->
			appendLine(
				"""
				|	DocArticle("/${entry.route}",
				|	    "${entry.date}",
				|	    "${entry.title.escapeQuotes()}",
				|	    "${entry.desc.escapeQuotes()}",
				|	    "${entry.navTitle.escapeQuotes()}",
				|	    ${entry.keywords.asCode()},
				|	    "${entry.dateModified}",
				|	    ${entry.slugs.asCode()},
				|	    ${entry.position ?: "null"}
				|   ),
				""".trimMargin()
			)
		}

		appendLine(")")
	}
}
