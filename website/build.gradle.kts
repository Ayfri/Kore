import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe
import org.commonmark.node.*
import java.net.HttpURLConnection
import java.net.URI
import kotlin.time.Duration.Companion.seconds

plugins {
	kotlin("multiplatform")
	kotlin("plugin.compose")
	id("kotlin-conventions")
	id("publish-conventions") apply false
	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
}

group = "io.github.ayfri.kore.website"
version = "1.0-SNAPSHOT"

val docGroupOrder =
	listOf("guides", "commands", "data-driven", "concepts", "helpers", "oop", "advanced", "contributing")

val minecraftVersion = providers.gradleProperty("minecraft.version").orElse("").get()

/** Plain-text GitHub data (stars, latest release tag) written by the fetch tasks for the Open Graph cards. */
val gitHubDataDir = layout.buildDirectory.dir("generated/github")

/** Open Graph cards only ship in the exported site, rendering them on every Markdown edit of the dev server costs seconds. */
val renderOgImages = gradle.startParameter.taskNames.any { it.substringAfterLast(':') == "kobwebExport" }

data class DocEntry(
	val file: File,
	val date: String,
	val title: String,
	val desc: String,
	val navTitle: String,
	val keywords: List<String>,
	val dateModified: String,
	val slugs: List<String>,
	val position: Int? = null,
)

kobweb {
	val projectGroup = group
	val projectLogger = logger

	app {
		globals.set(
			mapOf(
				"docGroupOrder" to docGroupOrder.joinToString(","),
				"minecraftVersion" to minecraftVersion,
				"projectVersion" to Project.VERSION,
				"websiteUrl" to Project.WEBSITE_URL,
			)
		)

		index {
			head.apply {
				add {
					script("text/javascript", "/prism.min.js") {
						attributes += "data-manual" to ""
					}
				}
			}
			head.add {
				link("https://fonts.googleapis.com", "preconnect")
				link("https://fonts.gstatic.com", "preconnect") {
					attributes["crossorigin"] = "anonymous"
				}
				link("https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400;500;600&family=JetBrains+Mono:ital,wght@0,400;0,700;1,400&family=Sora:wght@600;700&family=Roboto:wght@100;300;500;900&display=swap", "stylesheet")

				script("text/javascript", "https://www.googletagmanager.com/gtag/js?id=G-3ZXF56FSLH") {
					async = true
				}
				script("text/javascript") {
					unsafe {
						+"""function gtag(){dataLayer.push(arguments)}window.dataLayer=window.dataLayer||[],gtag("js",new Date),gtag("config","G-3ZXF56FSLH")"""
					}
				}
			}
		}

		export {
			includeSourceMap = false
			// Playwright's 30s default leaves no headroom once several pages are snapshotted at once.
			timeout = 90.seconds
		}
	}

	markdown {
		imports.add("com.varabyte.kobweb.compose.ui.modifiers.*")

		handlers {
			img.set { image ->
				val altText =
					image.children().filterIsInstance<Text>().joinToString("") { it.literal.escapeSingleQuotedText() }
				childrenOverride = emptyList()

				// `![Diamond](mc:item/diamond)` renders a vanilla texture inline.
				if (image.destination.startsWith("mc:")) {
					return@set """io.github.ayfri.kore.website.components.common.McSprite("${image.destination.removePrefix("mc:")}", "$altText")"""
				}

				"""org.jetbrains.compose.web.dom.Img(src="${image.destination}", alt="$altText") {
					|   attr("loading", "lazy")
					|   attr("decoding", "async")
					|}
				""".trimMargin()
			}

			code.set { code ->
				val text = "\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\""

				"""io.github.ayfri.kore.website.components.common.CodeBlock($text, "${code.info.takeIf { it.isNotBlank() }}")"""
			}

			blockquote.set { blockquote ->
				val firstChild = blockquote.firstChild
				if (firstChild is Paragraph) {
					val firstText = firstChild.firstChild as? Text
					if (firstText?.literal?.startsWith("[!") == true) {
						val calloutType = firstText.literal.substringAfter("[!").substringBefore("]").uppercase()
						val remainingText = firstText.literal.substringAfter("]").trim()

						if (remainingText.isEmpty()) {
							firstText.unlink()
						} else {
							firstText.literal = remainingText
						}

						return@set "io.github.ayfri.kore.website.components.common.Callout(\"$calloutType\")"
					}
				}

				"""org.jetbrains.compose.web.dom.Blockquote"""
			}

			heading.set { heading ->
				fun Node.plainText(): String = when (this) {
					is Text -> literal
					is Code -> literal
					is Link, is Emphasis, is StrongEmphasis -> children().joinToString("") { it.plainText() }
					else -> ""
				}

				fun Node.composeText(): String = when (this) {
					is Text -> "org.jetbrains.compose.web.dom.Text(\"${literal.escapeSingleQuotedText()}\")"
					is Code -> "org.jetbrains.compose.web.dom.Code { org.jetbrains.compose.web.dom.Text(\"${literal.escapeTripleQuotedText()}\") }"
					is Link -> {
						val linkContent = children().joinToString("\n") { child -> child.composeText() }
						"""org.jetbrains.compose.web.dom.A(href = "$destination") {
							|   $linkContent
							|}""".trimMargin()
					}

					is Emphasis -> {
						val emphasisContent = children().joinToString("\n") { child -> child.composeText() }
						"""org.jetbrains.compose.web.dom.Span(classes(io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle.italic)) {
							|   $emphasisContent
							|}""".trimMargin()
					}

					else -> ""
				}

				val id = heading.children().joinToString("") {
					val literal = it.plainText()
					if (literal.isBlank()) return@joinToString ""
					literal.lowercase().replace(Regex("[^a-z0-9]+"), "-")
				}

				// Ordered so "GitHub Actions" wins over "GitHub".
				val brandIcons = listOf(
					"CurseForge" to "curseforge",
					"GitHub Actions" to "githubactions",
					"GitHub" to "github",
					"Gradle" to "gradle",
					"IntelliJ" to "intellijidea",
					"Modrinth" to "modrinth",
					"Node.js" to "nodedotjs",
				)
				val headingText = heading.children().joinToString("") { it.plainText() }
				val brandIcon = brandIcons.firstOrNull { (brand) -> brand in headingText }
					?.let { (_, icon) -> "io.github.ayfri.kore.website.components.common.BrandIcon(\"$icon\")" }
					.orEmpty()

				val content = heading.children().joinToString("\n") { it.composeText() }

				childrenOverride = emptyList()
				val tag = "H${heading.level}"

				val onSubtitle =
					if (heading.level > 1) "classes(io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle.heading)"
					else ""

				val idAttribute = if (id.isNotBlank()) """attr("id", "$id")""" else ""

				"""org.jetbrains.compose.web.dom.${tag.replaceFirstChar { it.uppercase() }}({
					|   $idAttribute
					|   $onSubtitle
					|}) {
					|   org.jetbrains.compose.web.dom.A("#$id", {
					|	   classes(io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle.anchor)
					|   }) {
					|	   com.varabyte.kobweb.silk.components.icons.lucide.LucideHash(modifier = com.varabyte.kobweb.compose.ui.Modifier.ariaHidden())
					|   }
					|   $brandIcon
					|   $content
					|}
				""".trimMargin()
			}
		}

		// Capture values outside the callback for configuration cache compatibility
		val docGroupOrder = docGroupOrder
		val projectName = project.name
		val projectDir = project.projectDir
		val markdownDir = projectDir.resolve("src/jsMain/resources/markdown")
		// Kobweb flattens a "public" subfolder of resources to the site root, so the generated dir must mirror that layout for llms.txt/sitemap.xml/etc. to end up at the site root.
		val llmsResourcesDir = layout.buildDirectory.dir("generated/llms-resources/public").get().asFile
		val ogFontsDir = layout.buildDirectory.dir("og-fonts").get().asFile
		val gitHubDataDir = gitHubDataDir.get().asFile
		val minecraftVersion = minecraftVersion
		val renderOgImages = renderOgImages

		process.set { markdownFiles ->
			val docEntries = mutableListOf<DocEntry>()

			markdownFiles.forEach { docArticle ->
				val path = markdownDir.resolve(docArticle.filePath)
				val fileName = path.name
				val fm = docArticle.frontMatter
				val requiredFields =
					listOf("title", "description", "date-created", "date-modified", "nav-title", "routeOverride")
				val title = fm["title"]?.firstOrNull()
				val desc = fm["description"]?.firstOrNull()
				val dateCreated = fm["date-created"]?.firstOrNull()
				val dateModified = fm["date-modified"]?.firstOrNull()
				val navTitle = fm["nav-title"]?.firstOrNull()
				val routeOverride = fm["routeOverride"]?.firstOrNull()

				if (title == null || desc == null || dateCreated == null || dateModified == null || navTitle == null || routeOverride == null) {
					projectLogger.warn("Skipping '$fileName', missing required fields in front matter of $fileName: ${requiredFields.filter { fm[it] == null }}")
					return@forEach
				}

				val keywords = fm["keywords"]?.firstOrNull()?.split(Regex(",\\s*")) ?: emptyList()
				val position = fm["position"]?.firstOrNull()?.toIntOrNull()
				// Dates are formatted in ISO 8601 format
				val dateCreatedComplete = dateCreated.split("-").let { (year, month, day) ->
					"$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}T00:00:00Z"
				}
				val dateModifiedComplete = dateModified.split("-").let { (year, month, day) ->
					"$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}T00:00:00Z"
				}
				val slugs = routeOverride.split("/").drop(1)
				val newEntry = DocEntry(
					file = path,
					date = dateCreatedComplete,
					title = title,
					desc = desc,
					navTitle = navTitle,
					keywords = keywords,
					dateModified = dateModifiedComplete,
					slugs = slugs,
					position = position
				)
				docEntries += newEntry
			}

			fun getGroupPriority(slug: String) =
				docGroupOrder.indexOf(slug.lowercase()).takeIf { it >= 0 } ?: docGroupOrder.size

			// Sort entries by group priority, then optional position, then slug/title names.
			val sortedEntries = docEntries.sortedWith(Comparator { a, b ->
				val slugsA = a.slugs.drop(1)
				val slugsB = b.slugs.drop(1)
				val maxDepth = minOf(slugsA.size, slugsB.size)

				for (i in 0 until maxDepth) {
					val slugA = slugsA[i]
					val slugB = slugsB[i]

					// Apply fixed position only on leaf entries where explicitly set.
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

			// Generate llms.txt (documentation index for LLMs) grouped by sections
			val baseUrl = Project.WEBSITE_URL // Also available as AppGlobals["websiteUrl"]
			val llmsContent = buildString {
				appendLine("# $projectName Documentation")
				appendLine("> Documentation index generated for LLMs.")
				appendLine("Complete llms-full.txt available at: $baseUrl/llms-full.txt")
				appendLine()

				// Group entries by their first slug (section)
				val groupedEntries = sortedEntries.groupBy { it.slugs.getOrNull(1) ?: "other" }
				val orderedGroups = groupedEntries.keys.sortedBy { getGroupPriority(it) }

				orderedGroups.forEach { group ->
					val groupTitle = group.replace("-", " ").replaceFirstChar { it.uppercase() }
					appendLine("## $groupTitle")
					appendLine()

					groupedEntries[group]?.forEach { entry ->
						val route = entry.slugs.joinToString("/")
						append("- [${entry.title}]($baseUrl/$route)")
						if (entry.desc.isNotBlank()) append(": ${entry.desc}")
						appendLine()
					}
					appendLine()
				}
			}

			// Generate llms-full.txt (full raw content for LLMs) grouped by sections
			val llmsFullContent = buildString {
				appendLine("# $projectName Full Documentation")
				appendLine()

				val groupedEntries = sortedEntries.groupBy { it.slugs.getOrNull(1) ?: "other" }
				val orderedGroups = groupedEntries.keys.sortedBy { getGroupPriority(it) }

				orderedGroups.forEach { group ->
					val groupTitle = group.replace("-", " ").replaceFirstChar { it.uppercase() }
					appendLine("# $groupTitle")
					appendLine()

					groupedEntries[group]?.forEach { entry ->
						appendLine("## ${entry.title}")
						appendLine(entry.file.readText())
						appendLine("\n---\n")
					}
				}
			}

			// Write files to a generated resources directory (never checked into sources)
			llmsResourcesDir.mkdirs()
			llmsResourcesDir.resolve("llms.txt").writeText(llmsContent)
			llmsResourcesDir.resolve("llms-full.txt").writeText(llmsFullContent)

			val markdownSources = markdownFiles.associate { docArticle ->
				docArticle.filePath.replace('\\', '/') to markdownDir.resolve(docArticle.filePath).readText()
			}
			llmsResourcesDir.resolve("markdown-sources.json").writeText(JsonOutput.toJson(markdownSources))

			// Generate sitemap.xml
			val sitemap = buildString {
				appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
				appendLine("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")
				appendLine("""	<url>""")
				appendLine("""		<loc>$baseUrl/</loc>""")
				appendLine("""		<changefreq>weekly</changefreq>""")
				appendLine("""		<priority>1.0</priority>""")
				appendLine("""	</url>""")
				listOf("features", "updates").forEach { page ->
					appendLine("""	<url>""")
					appendLine("""		<loc>$baseUrl/$page</loc>""")
					appendLine("""		<changefreq>weekly</changefreq>""")
					appendLine("""		<priority>0.9</priority>""")
					appendLine("""	</url>""")
				}
				sortedEntries.forEach { entry ->
					val route = entry.slugs.joinToString("/")
					val priority = when {
						entry.slugs.size <= 2 -> "0.9"
						entry.slugs.size == 3 -> "0.7"
						else -> "0.5"
					}
					appendLine("""	<url>""")
					appendLine("""		<loc>$baseUrl/$route</loc>""")
					appendLine("""		<lastmod>${entry.dateModified.take(10)}</lastmod>""")
					appendLine("""		<changefreq>monthly</changefreq>""")
					appendLine("""		<priority>$priority</priority>""")
					appendLine("""	</url>""")
				}
				appendLine("</urlset>")
			}
			llmsResourcesDir.resolve("sitemap.xml").writeText(sitemap)

			println("Sitemap generated -> ${llmsResourcesDir.resolve("sitemap.xml").absolutePath}")

			// Open Graph cards, `/og/<route>.png` per doc page, `/og/features.png`, `/og/updates.png` and `/og/default.png` for the other pages.
			if (renderOgImages) {
				val ogDir = llmsResourcesDir.resolve("og")
				val host = baseUrl.substringAfter("://")
				val stars = gitHubDataDir.resolve("stars.txt").takeIf { it.exists() }?.readText()?.toIntOrNull()
				val ogRenderer = OgImageRenderer(ogFontsDir, projectDir.resolve("src/jsMain/resources/public/logo.png"), stars)
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
					val route = entry.slugs.joinToString("/")
					ogRenderer.render(
						ogDir.resolve("$route.png"),
						label = entry.slugs.dropLast(1).joinToString(" / ") { it.replace("-", " ") },
						title = entry.navTitle,
						description = entry.desc,
						footer = "$host/$route",
					)
				}
				println("Open Graph images generated -> ${ogDir.absolutePath}")
			}

			println("LLMs.txt generated -> ${llmsResourcesDir.absolutePath}")
			projectLogger.info("markdown-sources.json written (${markdownSources.size} files)")

			generateKotlin("$projectGroup/docEntries.kt", buildString {
				appendLine(
					"""
					|// This file is generated. Modify the build script if you need to change it.
					|
					|package io.github.ayfri.kore.website
					|
					|import io.github.ayfri.kore.website.components.doc.DocArticle
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
						|	DocArticle("/docs/${
							entry.file.path.substringBeforeLast(".md")
								.replace(Regex(" |_"), "-")
								.replace("\\", "/")
								.substringAfter("doc/")
								.lowercase()
						}",
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
					projectLogger.info("Generated entry for ${entry.file.name}")
				}

				appendLine(")")
				projectLogger.info("Generated ${docEntries.size} entries in docEntries.kt")
			})
		}
	}
}

tasks.register("fetchGitHubReleases") {
	group = "kore"
	description = "Fetches GitHub releases and generates a Kotlin file with the data"

	val outFile =
		layout.buildDirectory.file("generated/kore/src/jsMain/kotlin/io/github/ayfri/kore/website/gitHubReleases.kt")
	val latestTagFile = gitHubDataDir.map { it.file("latest-release-tag.txt") }
	outputs.files(outFile, latestTagFile)

	doLast {
		fun String.escapeForKotlinRawString() = replace("\"\"\"", "\\\"\\\"\\\"").replace("$", "${'$'}{'$'}")

		val allReleases = mutableListOf<Map<*, *>>()
		var page = 1
		var hasMorePages = true

		while (hasMorePages) {
			val apiUrl = "https://api.github.com/repos/Ayfri/Kore/releases?per_page=100&page=$page"
			val conn = URI(apiUrl).toURL().openConnection() as HttpURLConnection
			conn.requestMethod = "GET"
			conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
			conn.setRequestProperty("User-Agent", "KoreWebsite/1.0 (+https://kore.ayfri.com)")

			System.getenv("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }
				?.let { conn.setRequestProperty("Authorization", "token $it") }

			val code = conn.responseCode
			if (code != 200) {
				val err = conn.errorStream?.bufferedReader()?.use { it.readText() }
				logger.error("GitHub API returned $code for page $page. Body: $err")
				// We break the loop, but we will still write a file (possibly empty)
				break
			}

			val jsonResponse = conn.inputStream.bufferedReader().use { it.readText() }
			val pageReleases = JsonSlurper().parseText(jsonResponse) as List<Map<*, *>>
			allReleases += pageReleases

			hasMorePages = pageReleases.isNotEmpty() && pageReleases.size == 100
			page++
		}

		// Same pick as `GitHubService.latestRelease`, ISO timestamps sort chronologically as strings.
		val latestTag = allReleases.filter { it["published_at"] != null }.maxByOrNull { it["published_at"] as String }?.get("tag_name") as String?
		latestTagFile.get().asFile.apply { parentFile.mkdirs() }.writeText(latestTag.orEmpty())

		// Always write a file, even if it is empty
		val targetFile = outFile.get().asFile
		targetFile.parentFile.mkdirs()
		targetFile.writeText(buildString {
			appendLine("// This file is generated. Do not modify directly.")
			appendLine("package io.github.ayfri.kore.website")
			appendLine("")
			appendLine("import io.github.ayfri.kore.website.components.updates.GitHubAsset")
			appendLine("import io.github.ayfri.kore.website.components.updates.GitHubRelease")
			appendLine("")
			if (allReleases.isEmpty()) {
				appendLine("val gitHubReleases: List<GitHubRelease> = emptyList()")
				return@buildString
			}
			appendLine("val gitHubReleases = listOf(")

			try {
				allReleases.forEach { release ->
					val id = release["id"] as Number
					val name = (release["name"] as? String ?: "").replace("\"", "\\\"")
					val tagName = release["tag_name"] as String
					val htmlUrl = release["html_url"] as String
					val url = release["url"] as String
					val createdAt = release["created_at"] as String
					val publishedAt = release["published_at"] ?: return@forEach
					val body = (release["body"] as? String ?: "").escapeForKotlinRawString()
					val isPrerelease = release["prerelease"] as Boolean
					val assets = (release["assets"] as? List<Map<*, *>>).orEmpty()

					appendLine("	GitHubRelease(")
					appendLine("		id = $id,")
					appendLine("		name = \"$name\",")
					appendLine("		tagName = \"$tagName\",")
					appendLine("		htmlUrl = \"$htmlUrl\",")
					appendLine("		url = \"$url\",")
					appendLine("		createdAt = \"$createdAt\",")
					appendLine("		publishedAt = \"$publishedAt\",")
					appendLine("		body = \"\"\"$body\"\"\",")
					appendLine("		isPrerelease = $isPrerelease,")
					if (assets.isNotEmpty()) {
						appendLine("		assets = listOf(")
						assets.forEach { asset ->
							val assetId = asset["id"] as Number
							val assetName = (asset["name"] as? String ?: "").replace("\"", "\\\"")
							val browserDownloadUrl = asset["browser_download_url"] as String
							val contentType = asset["content_type"] as String
							val size = asset["size"] as Number
							val downloadCount = asset["download_count"] as Number

							appendLine("            GitHubAsset(")
							appendLine("                id = $assetId,")
							appendLine("                name = \"$assetName\",")
							appendLine("                browserDownloadUrl = \"$browserDownloadUrl\",")
							appendLine("                contentType = \"$contentType\",")
							appendLine("                size = $size,")
							appendLine("                downloadCount = $downloadCount")
							appendLine("            ),")
						}
						appendLine("        )")
					} else {
						appendLine("        assets = emptyList()")
					}
					appendLine("    ),")
				}
			} catch (e: Exception) {
				logger.error("Failed to generate GitHub releases file. Body: ${allReleases.joinToString("\n")}")
				throw e
			}
			appendLine(")")
		})

		logger.lifecycle("Generated GitHub releases file with ${allReleases.size} releases → ${targetFile.path}")
	}
}

tasks.register("fetchGitHubStars") {
	group = "kore"
	description = "Fetches the GitHub repository star count and generates a Kotlin file with the data"

	val outFile =
		layout.buildDirectory.file("generated/kore/src/jsMain/kotlin/io/github/ayfri/kore/website/gitHubStars.kt")
	val starsFile = gitHubDataDir.map { it.file("stars.txt") }
	outputs.files(outFile, starsFile)

	doLast {
		val apiUrl = "https://api.github.com/repos/Ayfri/Kore"
		val conn = URI(apiUrl).toURL().openConnection() as HttpURLConnection
		conn.requestMethod = "GET"
		conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
		conn.setRequestProperty("User-Agent", "KoreWebsite/1.0 (+https://kore.ayfri.com)")

		System.getenv("GITHUB_TOKEN")?.takeIf { it.isNotBlank() }
			?.let { conn.setRequestProperty("Authorization", "token $it") }

		val stars = try {
			val code = conn.responseCode
			if (code != 200) {
				val err = conn.errorStream?.bufferedReader()?.use { it.readText() }
				logger.error("GitHub API returned $code for repo stars. Body: $err")
				null
			} else {
				val jsonResponse = conn.inputStream.bufferedReader().use { it.readText() }
				(JsonSlurper().parseText(jsonResponse) as Map<*, *>)["stargazers_count"] as? Int
			}
		} catch (e: Exception) {
			logger.error("Failed to fetch GitHub repository stars.", e)
			null
		}

		starsFile.get().asFile.apply { parentFile.mkdirs() }.writeText(stars?.toString().orEmpty())

		val targetFile = outFile.get().asFile
		targetFile.parentFile.mkdirs()
		targetFile.writeText(buildString {
			appendLine("// This file is generated. Do not modify directly.")
			appendLine("package io.github.ayfri.kore.website")
			appendLine("")
			appendLine("val gitHubStars: Int? = $stars")
		})

		logger.lifecycle("Generated GitHub stars file (stars = $stars) → ${targetFile.path}")
	}
}

tasks.named("kobwebExport") {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars")
}

// Ensure generated sources exist before KSP for JS runs
tasks.matching { it.name == "kspKotlinJs" }.configureEach {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars")
}

tasks.matching { it.name == "compileKotlinJs" }.configureEach {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars")
}

// llms.txt/sitemap.xml/markdown-sources.json are written by kobwebxMarkdownProcess into a
// generated resources dir; make the dependency explicit so caching/ordering can't skip them.
tasks.matching { it.name == "jsProcessResources" }.configureEach {
	dependsOn("kobwebxMarkdownProcess")
}

// The Open Graph cards rendered by kobwebxMarkdownProcess show the star count and the latest release.
tasks.matching { it.name == "kobwebxMarkdownProcess" }.configureEach {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars")
	inputs.dir(gitHubDataDir).withPropertyName("gitHubData").optional()
	inputs.property("renderOgImages", renderOgImages)
}

// The export discards the source map (`includeSourceMap = false`), so building it only slows minification down.
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>()
	.matching { it.name == "jsBrowserProductionWebpack" }
	.configureEach { sourceMaps = false }

kotlin {
	configAsKobwebApplication("website")

	js {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}

		compilerOptions {
			target = "es2015"
			useEsClasses = true
		}
		useEsModules()
		binaries.executable()
	}

	sourceSets {
		jsMain {
			kotlin.srcDir("build/generated/kore/src/jsMain/kotlin")
			resources.srcDir(layout.buildDirectory.dir("generated/llms-resources"))

			dependencies {
				// Minifier for the production bundle, see `webpack.config.d/00-bundle-speed.js`.
				implementation(devNpm("@swc/core", libs.versions.swc.get()))
			}
		}
		commonMain {
			dependencies {
				implementation(libs.compose.html.core)
				implementation(libs.compose.runtime)
				implementation(libs.kobweb.core)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobwebx.silk.icons.lucide)
				implementation(npm("marked", libs.versions.marked.get()))
			}
		}
	}
}
