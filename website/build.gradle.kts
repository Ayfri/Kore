import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import com.varabyte.kobwebx.gradle.markdown.handlers.NodeScope
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import kotlinx.html.LinkAs
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.unsafe
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.node.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
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

/** GitHub data written by the fetch tasks (stars, releases, Minecraft versions data), read by the Open Graph cards, `llms.txt` and the doc placeholders. */
val gitHubDataDir = layout.buildDirectory.dir("generated/github")

/** The playground example list and each example's pack, written by `:playground-examples:generatePlaygroundExamples`. */
val playgroundExamplesDir = layout.projectDirectory.dir("playground-examples/build/generated/playground")

/** `public/playground-api.json`, the Kore API the playground editor completes, written by `:playground-examples:generatePlaygroundApiIndex`. */
val playgroundApiDir = layout.projectDirectory.dir("playground-examples/build/generated/playground-api")

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

/** A GitHub release read back from `releases.tsv`, tagged `v<kore version>-<minecraft version>`. */
data class ReleaseEntry(val tag: String, val prerelease: Boolean, val published: String) {
	val coordinate = tag.removePrefix("v")
	val koreVersion = coordinate.substringBefore('-')
	val minecraftVersion = coordinate.substringAfter('-', "")

	/** Same rule as the site's version matrix: a plain Minecraft release (`26.2`, `1.21.4`), never a snapshot, pre-release or release candidate. */
	val isStable = !prerelease && Regex("""\d+\.\d+(\.\d+)?""").matches(minecraftVersion)

	/** Zero-padded numbers, so plain string comparison orders `26.10` after `26.2`. */
	val minecraftSortKey = minecraftVersion.split('.').joinToString(".") { it.padStart(4, '0') }
}

kobweb {
	val projectGroup = group
	val projectLogger = logger

	app {
		globals.set(
			mapOf(
				"docGroupOrder" to docGroupOrder.joinToString(","),
				"minecraftVersion" to minecraftVersion,
				// Compile backend for /playground. Empty here: the page stays usable and says compiling is off.
				"playgroundApiUrl" to (findProperty("kore.playgroundApiUrl") as String? ?: ""),
				"projectVersion" to Project.VERSION,
				"websiteUrl" to Project.WEBSITE_URL,
			)
		)

		index {
			// Deferred scripts run in document order, so the deferred Prism of the head is ready before the app starts.
			scriptAttributes.put("defer", "")

			head.add {
				// The exported pages paint without the app, but its bundle sits at the end of a body that reaches 500 KB on /updates.
				link("/website.js", "preload", htmlAs = LinkAs.script)
				// The text and heading fonts show above the fold on every page, the code font only loads once a code block renders.
				for (file in listOf("ibm-plex-sans", "sora")) {
					link("/fonts/$file.woff2", "preload", "font/woff2", LinkAs.font) {
						attributes["crossorigin"] = "anonymous"
					}
				}
				// Latin subsets of Google Fonts' variable files, other glyphs fall back to the next font of the stack.
				style {
					unsafe {
						+"""
							@font-face{font-display:swap;font-family:"IBM Plex Sans";font-weight:400 700;src:url(/fonts/ibm-plex-sans.woff2) format("woff2")}
							@font-face{font-display:swap;font-family:"JetBrains Mono";font-style:italic;font-weight:400;src:url(/fonts/jetbrains-mono-italic.woff2) format("woff2")}
							@font-face{font-display:swap;font-family:"JetBrains Mono";font-weight:400 700;src:url(/fonts/jetbrains-mono.woff2) format("woff2")}
							@font-face{font-display:swap;font-family:"Sora";font-weight:600 700;src:url(/fonts/sora.woff2) format("woff2")}
						""".trimIndent().replace("\n", "")
					}
				}
				link("/view-transitions.css", "stylesheet")
				link("/llms.txt", "describedby")

				script("text/javascript", "/prism.min.js") {
					attributes["data-manual"] = ""
					defer = true
				}

				// `pagereveal` fires before website.js runs: turns the doc direction `PageTransitions` stores on `pageswap` into a transition type.
				script("text/javascript") {
					unsafe {
						+"""addEventListener("pagereveal",e=>{const h=sessionStorage.getItem("kore-view-transition");if(h===null)return;sessionStorage.removeItem("kore-view-transition");const[t,p]=h.split(" ");if(p===location.pathname)e.viewTransition?.types?.add(t)})"""
					}
				}

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

		/** Doc placeholders like `{{kore.release}}`, listed in the "Documentation pages" section of `contributing/Architecture_and_Patterns.md`. */
		val docConstantRegex = Regex("""\{\{((?:java|kore|mc)\.\w+)}}""")

		/** `kore.release` -> `koreRelease`, its property in the generated `DocConstants`. */
		fun docConstantProperty(key: String) = key.split('.').let { (prefix, name) -> prefix + name.replaceFirstChar { it.uppercase() } }

		handlers {
			/** Placeholders become templates reading the `DocConstants` generated by `kobwebxMarkdownProcess`, once the release data is fetched. */
			fun String.withDocConstants() = replace(docConstantRegex) { "\${io.github.ayfri.kore.website.DocConstants.${docConstantProperty(it.groupValues[1])}}" }

			/** A node holding a single text or inline code becomes one `MarkdownLeaf` call instead of a composable lambda, ~6 MB less JS over all pages. */
			fun NodeScope.leaf(node: Node, tag: String, fallback: String, extraArgs: String = ""): String {
				val child = node.firstChild?.takeIf { it.next == null }
				val (text, code) = when (child) {
					is Text -> child.literal to false
					is Code -> child.literal to true
					else -> return fallback
				}
				childrenOverride = emptyList()
				val codeArg = if (code) ", code = true" else ""
				return """io.github.ayfri.kore.website.components.layouts.MarkdownLeaf("$tag", "${text.escapeSingleQuotedText().withDocConstants()}"$codeArg$extraArgs)"""
			}

			fun NodeScope.cell(cell: TableCell, tag: String): String {
				val align = cell.alignment?.name?.lowercase()
				val fallback = "org.jetbrains.compose.web.dom.${tag.replaceFirstChar { it.uppercase() }}" +
					align?.let { "(attrs = { style { property(\"text-align\", \"$it\") } })" }.orEmpty()
				return leaf(cell, tag, fallback, align?.let { ", align = \"$it\"" }.orEmpty())
			}

			a.set { link ->
				val href = link.destination.escapeSingleQuotedText().withDocConstants()
				leaf(link, "a", "org.jetbrains.compose.web.dom.A(\"$href\")", ", href = \"$href\"")
			}
			em.set { leaf(it, "em", "org.jetbrains.compose.web.dom.Em") }
			p.set { leaf(it, "p", "org.jetbrains.compose.web.dom.P") }
			strong.set { leaf(it, "b", "org.jetbrains.compose.web.dom.B") }
			td.set { cell(it, "td") }
			th.set { cell(it, "th") }

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
				val text = "\"\"\"${code.literal.escapeTripleQuotedText().withDocConstants()}\"\"\""

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
							if (firstChild.firstChild == null) firstChild.unlink()
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
					is Code -> "io.github.ayfri.kore.website.components.layouts.InlineCode(\"${literal.escapeSingleQuotedText()}\")"
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
				}.trim('-')

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
				val brandIcon = brandIcons.firstOrNull { (brand) -> brand in headingText }?.let { (_, icon) -> ", \"$icon\"" }.orEmpty()

				val content = heading.children().joinToString("\n") { it.composeText() }

				childrenOverride = emptyList()

				"""io.github.ayfri.kore.website.components.layouts.MarkdownHeading(${heading.level}, "$id"$brandIcon) {
					|   $content
					|}
				""".trimMargin()
			}

			inlineCode.set { code ->
				childrenOverride = emptyList()
				"io.github.ayfri.kore.website.components.layouts.InlineCode(\"${code.literal.escapeSingleQuotedText().withDocConstants()}\")"
			}

			text.set { text -> "org.jetbrains.compose.web.dom.Text(\"${text.literal.escapeSingleQuotedText().withDocConstants()}\")" }
		}

		// Capture values outside the callback for configuration cache compatibility
		val docGroupOrder = docGroupOrder
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

			val baseUrl = Project.WEBSITE_URL // Also available as AppGlobals["websiteUrl"]
			val githubUrl = "https://${Project.GITHUB_URL}"
			val summary = "Kore is an open-source Kotlin DSL and library that generates type-safe Minecraft Java Edition datapacks: functions, commands, loot tables, recipes, advancements, worldgen and more are written as Kotlin code instead of JSON and MCFunction by hand."

			val mavenKore = "${Project.GROUP}:kore"
			val releases = gitHubDataDir.resolve("releases.tsv").takeIf { it.exists() }?.readLines().orEmpty()
				.filter { it.isNotBlank() }
				.map { line -> line.split('\t').let { (tag, prerelease, published) -> ReleaseEntry(tag, prerelease.toBoolean(), published) } }
				.filter { it.minecraftVersion.isNotEmpty() }
				.sortedByDescending { it.published }
			val latestStable = releases.firstOrNull { it.isStable }
			// The newest release overall, when it is not stable, is the pre-release newer than the latest stable one.
			val latestPreRelease = releases.firstOrNull()?.takeIf { !it.isStable }

			// The dev server falls back on `minecraft.version`, the deployed site must never show a snapshot as the latest stable release.
			if (latestStable == null && renderOgImages) {
				throw GradleException("No stable GitHub release in releases.tsv, the doc placeholders would show Minecraft $minecraftVersion as the latest stable version.")
			}
			val stableMinecraftVersion = latestStable?.minecraftVersion ?: minecraftVersion
			val versionsFile = gitHubDataDir.resolve("versions_data.json")
			val versionsData = versionsFile.takeIf { it.exists() }?.let { JsonSlurper().parseText(it.readText()) as Map<*, *> }.orEmpty()
			fun Map<*, *>?.field(key: String) = this?.get(key)?.toString()
				?: throw GradleException("No `$key` for Minecraft $stableMinecraftVersion in $versionsFile, rerun `fetchMinecraftVersions --rerun`.")
			val stableVersionData = versionsData[stableMinecraftVersion] as? Map<*, *>
			// A plain integer before 1.21.9, a `{major, minor}` object since.
			val dataPackFormat = stableVersionData?.get("data_pack")?.let { it as? Map<*, *> ?: mapOf("major" to it, "minor" to 0) }
			val docConstants = mapOf(
				"java.version" to stableVersionData.field("java"),
				"kore.mavenSnapshot" to "${Project.VERSION}-$minecraftVersion-SNAPSHOT",
				"kore.release" to (latestStable?.coordinate ?: "${Project.VERSION}-$minecraftVersion"),
				"kore.snapshot" to ((latestPreRelease ?: latestStable)?.coordinate ?: "${Project.VERSION}-$minecraftVersion"),
				"kore.version" to (latestStable?.koreVersion ?: Project.VERSION),
				"mc.packFormatMajor" to dataPackFormat.field("major"),
				"mc.packFormatMinor" to dataPackFormat.field("minor"),
				"mc.version" to stableMinecraftVersion,
			)
			fun String.expandDocConstants() = replace(docConstantRegex) { docConstants.getValue(it.groupValues[1]) }

			generateKotlin("$projectGroup/DocConstants.kt", buildString {
				appendLine("// This file is generated. Modify the build script if you need to change it.")
				appendLine()
				appendLine("package io.github.ayfri.kore.website")
				appendLine()
				appendLine("/** Values of the doc placeholders, `{{kore.release}}` reading [koreRelease]. */")
				appendLine("object DocConstants {")
				docConstants.forEach { (key, value) -> appendLine("\tconst val ${docConstantProperty(key)} = \"$value\"") }
				appendLine("}")
			})

			/** One row per Minecraft drop like the home page widget: hotfixes fold into their `26.x`, or into the same Kore version before 26. */
			val stableRows = releases.filter { it.isStable }
				.distinctBy { it.minecraftVersion }
				.groupBy { if (it.minecraftVersion.startsWith("1.")) "kore:${it.koreVersion}" else "mc:${it.minecraftVersion.split('.').take(2).joinToString(".")}" }
				.map { (_, rows) -> rows.maxBy { it.minecraftSortKey } }
				.sortedByDescending { it.minecraftSortKey }
			val versionMatrix = if (releases.isEmpty()) "Every version is listed at $baseUrl/updates.\n" else buildString {
				appendLine("| Minecraft version | Kore version | Type | Gradle coordinate | Published |")
				appendLine("| --- | --- | --- | --- | --- |")
				appendLine("| $minecraftVersion | ${Project.VERSION} | Maven Snapshot | `$mavenKore:${Project.VERSION}-$minecraftVersion-SNAPSHOT` | master (continuous) |")
				latestPreRelease?.let { appendLine("| ${it.minecraftVersion} | ${it.koreVersion} | Snapshot | `$mavenKore:${it.coordinate}` | ${it.published.take(10)} |") }
				stableRows.forEach { appendLine("| ${it.minecraftVersion} | ${it.koreVersion} | Stable | `$mavenKore:${it.coordinate}` | ${it.published.take(10)} |") }
			}
			// The home page topic cards, read from the widget source so the links can't drift from it.
			val featureGrid = projectDir.resolve("src/jsMain/kotlin/io/github/ayfri/kore/website/components/doc/FeatureCards.kt").readText()
				.substringAfter("fun FeatureGrid()")
				.split("FeatureCard(").drop(1)
				.mapNotNull { card ->
					val title = Regex("""title = "([^"]*)"""").find(card)?.groupValues?.get(1) ?: return@mapNotNull null
					val links = Regex("""FeatureLink\("([^"]*)", "([^"]*)"\)""").findAll(card).joinToString(", ") { "[${it.groupValues[2]}](${it.groupValues[1]})" }
					"- **$title**: $links"
				}.joinToString("\n", postfix = "\n")
			val widgets = mapOf("FeatureGrid" to featureGrid, "VersionMatrix" to versionMatrix)
			val routes = sortedEntries.map { it.slugs.joinToString("/") }.toSet()

			val frontMatterRegex = Regex("""\A---\r?\n.*?\r?\n---\r?\n""", RegexOption.DOT_MATCHES_ALL)
			val fenceRegex = Regex("""(?ms)^[ \t]*(?<fence>`{3,}|~{3,}).*?^[ \t]*\k<fence>[`~]*[ \t]*$""")
			val widgetRegex = Regex("""(?m)^\{\{\{\s*([^\s}]+)\s*\}\}\}[ \t]*\r?\n?""")
			val spriteRegex = Regex("""(?<!`)!\[[^\]]*]\(mc:[^)]*\) ?""")
			val docLinkRegex = Regex("""]\(/(docs/[^)#\s]*)(#[^)\s]*)?\)""")
			val rootRelativeRegex = Regex("""]\(/(?!/)""")
			val blankRunRegex = Regex("""\n{3,}""")
			val headingRegex = Regex("""(?m)^#{1,6} .*""")
			/** Also catches the misspellings `docConstantRegex` skips, `{{ kore.release }}` or `{{minecraft.version}}`. */
			val docConstantLikeRegex = Regex("""\{\{\s*(?:java|kore|mc|minecraft)\b[^}]*}}""")

			val markdownSources = markdownFiles.associate { docArticle ->
				docArticle.filePath.replace('\\', '/') to markdownDir.resolve(docArticle.filePath).readText()
			}
			val docConstantErrors = markdownSources.flatMap { (path, source) ->
				val frontMatter = frontMatterRegex.find(source)?.value.orEmpty()
				val headings = headingRegex.findAll(source.replace(fenceRegex, "")).joinToString("\n") { it.value }
				docConstantLikeRegex.findAll(source)
					.filter { docConstantRegex.matchEntire(it.value)?.groupValues?.get(1) !in docConstants }
					.map { "$path: unknown placeholder ${it.value}" }.toList() +
					listOfNotNull(
						docConstantRegex.find(frontMatter)?.let { "$path: ${it.value} in the frontmatter, which no handler expands" },
						docConstantRegex.find(headings)?.let { "$path: ${it.value} in a heading, whose anchor would change on every release" },
					)
			}
			if (docConstantErrors.isNotEmpty()) {
				throw GradleException(docConstantErrors.joinToString("\n", "Invalid doc placeholders, the known ones are ${docConstants.keys}:\n"))
			}

			/** Outside code fences: widgets become Markdown (mockups vanish), `mc:` sprites go, root-relative links turn absolute. */
			fun String.toPlainMarkdown(): String {
				val fences = fenceRegex.findAll(this).map { it.value }.toList()
				val prose = fenceRegex.split(this).map { chunk ->
					chunk
						.replace(widgetRegex) { widgets[it.groupValues[1].substringAfterLast('.')].orEmpty() }
						.replace(spriteRegex, "")
						.replace(docLinkRegex) { if (it.groupValues[1] in routes) "]($baseUrl/${it.groupValues[1]}.md${it.groupValues[2]})" else it.value }
						.replace(rootRelativeRegex, "]($baseUrl/")
						.replace(blankRunRegex, "\n\n")
				}
				return prose.indices.joinToString("") { prose[it] + fences.getOrElse(it) { "" } }
			}

			/** The page as clean Markdown: its own H1 (the frontmatter title when it has none), the description, the update date and the canonical URL, then the body. */
			fun DocEntry.toMarkdown(): String {
				val body = file.readText().replace(frontMatterRegex, "").expandDocConstants().toPlainMarkdown().trim()
				val ownHeading = body.startsWith("# ")
				return buildString {
					appendLine("# ${if (ownHeading) body.substringBefore('\n').removePrefix("# ") else title}")
					appendLine()
					appendLine("> $desc")
					appendLine()
					appendLine("Updated ${dateModified.take(10)}. Canonical: $baseUrl/${slugs.joinToString("/")}")
					appendLine()
					append(if (ownHeading) body.substringAfter('\n', "").trimStart() else body)
				}
			}

			// Group entries by their first slug (section)
			val groupedEntries = sortedEntries.groupBy { it.slugs.getOrNull(1) ?: "other" }
			// Single-page sections outside `docGroupOrder` (home, getting started) come first.
			val orderedGroups = groupedEntries.keys.sortedBy { if (it in docGroupOrder) getGroupPriority(it) else -1 }

			val groupLabels = mapOf("data-driven" to "Data-driven", "getting-started" to "Getting started", "oop" to "OOP")
			val latestVersions = latestStable?.let { stable ->
				val preRelease = latestPreRelease?.let { ", the latest pre-release is `$mavenKore:${it.coordinate}` for Minecraft ${it.minecraftVersion}" }.orEmpty()
				"The latest stable release is `$mavenKore:${stable.coordinate}` for Minecraft ${stable.minecraftVersion}$preRelease."
			} ?: "The current versions are listed at $baseUrl/updates."

			// llms.txt: the llmstxt.org index, every page linking to its Markdown copy.
			val llmsContent = buildString {
				appendLine("# Kore")
				appendLine()
				appendLine("> $summary")
				appendLine()
				appendLine("Kore is published on Maven Central under the group `io.github.ayfri.kore` (artifacts `kore`, `oop`, `helpers` and `bindings`, all Kotlin Multiplatform for the JVM and JavaScript). $latestVersions Releases are versioned `<kore version>-<minecraft version>`, a Gradle project adds one with `implementation(\"$mavenKore:<version>\")` and the Getting Started page covers the setup. Source code, issues and releases: $githubUrl")
				appendLine()
				appendLine("Every documentation page below links to its Markdown copy, the same URL without the `.md` suffix is the HTML page.")
				appendLine()

				orderedGroups.forEach { group ->
					appendLine("## ${groupLabels[group] ?: group.replaceFirstChar { it.uppercase() }}")
					appendLine()

					groupedEntries[group]?.forEach { entry ->
						append("- [${entry.title}]($baseUrl/${entry.slugs.joinToString("/")}.md)")
						if (entry.desc.isNotBlank()) append(": ${entry.desc}")
						appendLine()
					}
					appendLine()
				}

				appendLine("## Optional")
				appendLine()
				appendLine("- [Full documentation]($baseUrl/llms-full.txt): every page in one file")
				appendLine("- [Playground]($baseUrl/playground): write and run Kore code in the browser")
				appendLine("- [Releases]($baseUrl/updates): changelog of every Kore version")
				appendLine("- [GitHub repository]($githubUrl): source code, issues and releases")
				appendLine("- [Kore Template](https://github.com/Kore-Minecraft/Kore-Template): ready-to-run starter project")
				appendLine("- [Kore Skill](https://github.com/Kore-Minecraft/Kore-Skill): agent skill for writing Kore code")
				appendLine("- [Discord](https://discord.ayfri.com): community support")
			}

			// llms-full.txt: the same pages as the .md copies, concatenated in the llms.txt order.
			val pageMarkdown = orderedGroups.flatMap { groupedEntries.getValue(it) }.associateWith { it.toMarkdown() }
			val llmsFullContent = "# Kore documentation\n\n> $summary\n\n" + pageMarkdown.values.joinToString("\n\n---\n\n") + "\n"

			// Write files to a generated resources directory (never checked into sources)
			llmsResourcesDir.mkdirs()
			llmsResourcesDir.resolve("llms.txt").writeText(llmsContent)
			llmsResourcesDir.resolve("llms-full.txt").writeText(llmsFullContent)

			llmsResourcesDir.resolve("docs").deleteRecursively()
			pageMarkdown.forEach { (entry, markdown) ->
				llmsResourcesDir.resolve("${entry.slugs.joinToString("/")}.md").apply { parentFile.mkdirs() }.writeText(markdown + "\n")
			}

			llmsResourcesDir.resolve("markdown-sources.json").writeText(JsonOutput.toJson(markdownSources.mapValues { it.value.expandDocConstants() }))

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

// Monaco's ESM build is bundled ahead of time by esbuild into plain static assets, never by Kobweb's
// webpack: `kobwebExport` copies exactly one script file, so any webpack code-split chunk would 404 in
// production, and a static import would instead drag all ~6 MB of Monaco into the main bundle on every page.
// The scripts are staged next to the Kotlin/JS `node_modules` so bare imports like `monaco-editor/...`
// resolve, for both esbuild and Node itself.
val stageMonacoBuild = tasks.register<Copy>("stageMonacoBuild") {
	group = "kore"
	description = "Stages the Monaco esbuild scripts next to the Kotlin/JS node_modules."

	dependsOn(rootProject.tasks.named("kotlinNpmInstall"))

	from(projectDir.resolve("monaco"))
	into(rootProject.layout.buildDirectory.dir("js/monaco-build"))
}

val bundleMonaco = tasks.register<Exec>("bundleMonaco") {
	group = "kore"
	description = "Bundles Monaco's ESM distribution into the public resources, served at /monaco."

	dependsOn(stageMonacoBuild)

	val buildDir = rootProject.layout.buildDirectory.dir("js/monaco-build")
	val outDir = projectDir.resolve("src/jsMain/resources/public/monaco")
	val nodeExecutable = rootProject.extensions.getByType<NodeJsEnvSpec>().executable

	inputs.dir(projectDir.resolve("monaco"))
	inputs.property("monacoVersion", libs.versions.monaco.editor.get())
	outputs.dir(outDir)

	workingDir(buildDir)
	commandLine(nodeExecutable.get(), "build-monaco.mjs", outDir.absolutePath)
}

tasks.matching { it.name == "jsProcessResources" }.configureEach {
	dependsOn(bundleMonaco)
}

tasks.register("fetchGitHubReleases") {
	group = "kore"
	description = "Fetches GitHub releases and generates a Kotlin file with the data"

	val outFile =
		layout.buildDirectory.file("generated/kore/src/jsMain/kotlin/io/github/ayfri/kore/website/gitHubReleases.kt")
	val latestTagFile = gitHubDataDir.map { it.file("latest-release-tag.txt") }
	val releasesFile = gitHubDataDir.map { it.file("releases.tsv") }
	outputs.files(outFile, latestTagFile, releasesFile)

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

		// `tag<TAB>prerelease<TAB>published_at` per published release, newest first, for the llms.txt versions and the home page version matrix.
		releasesFile.get().asFile.writeText(
			allReleases.filter { it["published_at"] != null }
				.sortedByDescending { it["published_at"] as String }
				.joinToString("") { "${it["tag_name"]}\t${it["prerelease"]}\t${it["published_at"]}\n" }
		)

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

tasks.register("fetchMinecraftVersions") {
	group = "kore"
	description = "Fetches the pack format and Java version of every Minecraft version, read by the doc placeholders"

	val versionsFile = gitHubDataDir.map { it.file("versions_data.json") }
	outputs.file(versionsFile)

	doLast {
		try {
			val conn = URI("https://raw.githubusercontent.com/PixiGeko/Minecraft-generated-data/master/versions_data.json").toURL().openConnection() as HttpURLConnection
			conn.connectTimeout = 10_000
			conn.readTimeout = 30_000
			conn.setRequestProperty("User-Agent", "KoreWebsite/1.0 (+https://kore.ayfri.com)")
			if (conn.responseCode != 200) {
				logger.error("PixiGeko/Minecraft-generated-data returned ${conn.responseCode} for versions_data.json, keeping the previous file.")
				return@doLast
			}
			versionsFile.get().asFile.apply { parentFile.mkdirs() }.writeBytes(conn.inputStream.use { it.readBytes() })
			logger.lifecycle("Fetched Minecraft versions data → ${versionsFile.get().asFile.path}")
		} catch (e: java.io.IOException) {
			logger.error("Failed to fetch versions_data.json, keeping the previous file.", e)
		}
	}
}

tasks.named("kobwebExport") {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars", bundleMonaco)
}

// Ensure generated sources exist before KSP for JS runs
tasks.matching { it.name == "kspKotlinJs" }.configureEach {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars", ":playground-examples:generatePlaygroundExamples")
}

tasks.matching { it.name == "compileKotlinJs" }.configureEach {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars", ":playground-examples:generatePlaygroundExamples")
}

tasks.matching { it.name == "jsProcessResources" }.configureEach {
	dependsOn(":playground-examples:generatePlaygroundApiIndex", ":playground-examples:generatePlaygroundExamples")
}

// llms.txt, llms-full.txt, the per-page .md copies, sitemap.xml and markdown-sources.json are written by
// kobwebxMarkdownProcess into a generated resources dir, which carries no task dependency as a source set.
tasks.matching { it.name == "jsProcessResources" }.configureEach {
	dependsOn("kobwebxMarkdownProcess")
}

// The Open Graph cards rendered by kobwebxMarkdownProcess show the star count and the latest release, the doc placeholders read the releases and the pack formats.
tasks.matching { it.name == "kobwebxMarkdownProcess" }.configureEach {
	dependsOn("fetchGitHubReleases", "fetchGitHubStars", "fetchMinecraftVersions")
	inputs.dir(gitHubDataDir).withPropertyName("gitHubData").optional()
	inputs.property("minecraftVersion", minecraftVersion)
	inputs.property("projectVersion", Project.VERSION)
	inputs.property("renderOgImages", renderOgImages)
	/** The `process` callback generating the files lives in this script and Gradle can't fingerprint a lambda. */
	inputs.file(buildFile).withPropertyName("generatorScript")
	inputs.file(layout.projectDirectory.file("src/jsMain/kotlin/io/github/ayfri/kore/website/components/doc/FeatureCards.kt")).withPropertyName("featureCards")
	outputs.dir(layout.buildDirectory.dir("generated/llms-resources")).withPropertyName("llmsResources")
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
			kotlin.srcDir(playgroundExamplesDir.dir("kotlin"))
			resources.srcDir(layout.buildDirectory.dir("generated/llms-resources"))
			resources.srcDir(playgroundApiDir.dir("resources"))
			resources.srcDir(playgroundExamplesDir.dir("resources"))

			dependencies {
				// Minifier for the production bundle, see `webpack.config.d/00-bundle-speed.js`.
				implementation(devNpm("@swc/core", libs.versions.swc.get()))
				// Monaco is pre-bundled by esbuild into static assets (see bundleMonaco) and never imported
				// from Kotlin, so both packages are build-time only.
				implementation(devNpm("esbuild", libs.versions.esbuild.get()))
				implementation(devNpm("monaco-editor", libs.versions.monaco.editor.get()))
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
