import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import groovy.json.JsonSlurper
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.Link
import org.commonmark.node.Text
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import java.net.HttpURLConnection
import java.net.URI

plugins {
	kotlin("multiplatform")
	kotlin("plugin.compose")
	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
}

group = "io.github.ayfri.kore.website"
version = "1.0-SNAPSHOT"

kobweb {
	val projectGroup = group
	val projectLogger = logger

	app {
		index {
			head.apply {
				add {
					link("/prism.min.css", "stylesheet")
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
				link("https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;500;900&display=swap", "stylesheet")

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
		}
	}

	markdown {
		handlers {
			img.set { image ->
				val altText = image.children()
					.filterIsInstance<Text>()
					.map { it.literal.escapeSingleQuotedText() }
					.joinToString("")
				childrenOverride = emptyList()

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

			heading.set { heading ->
				val id = heading.children()
					.map {
						val literal = when (it) {
							is Text -> it.literal
							is Code -> it.literal
							is Link -> it.destination
							is Emphasis -> (it.firstChild as Text).literal
							else -> ""
						}
						if (literal.isBlank()) return@map ""
						literal.lowercase().replace(Regex("[^a-z0-9]+"), "-")
					}
					.joinToString("")
				val content = heading.children()
					.map {
						when (it) {
							is Text -> "org.jetbrains.compose.web.dom.Text(\"${it.literal.escapeSingleQuotedText()}\")"
							is Code -> "org.jetbrains.compose.web.dom.Code { org.jetbrains.compose.web.dom.Text(\"${it.literal.escapeTripleQuotedText()}\") }"
							is Link -> "org.jetbrains.compose.web.dom.A(href = \"${it.destination}\") { org.jetbrains.compose.web.dom.Text(\"${
								it.children().joinToString("") { (it as Text).literal.escapeSingleQuotedText() }
							}\") }"

							is Emphasis ->
								if (it.firstChild is Text) "org.jetbrains.compose.web.dom.Span(classes(io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle.italic)) { org.jetbrains.compose.web.dom.Text(\"${(it.firstChild as Text).literal.escapeSingleQuotedText()}\") }"
								else ""

							else -> ""
						}
					}
					.joinToString("")
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
					|       classes(io.github.ayfri.kore.website.components.layouts.MarkdownLayoutStyle.anchor)
					|   }) {
					|       com.varabyte.kobweb.silk.components.icons.mdi.MdiLink()
					|   }
					|   $content
					|}
				""".trimMargin()
			}
		}

		process = { markdownFiles ->
			val docEntries = mutableListOf<DocEntry>()

			markdownFiles.forEach { docArticle ->
				val path = File(docArticle.filePath)
				val fileName = path.name
				val fm = docArticle.frontMatter
				val requiredFields = listOf("title", "description", "date-created", "date-modified", "nav-title", "routeOverride")
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

				// THIS SEEMS TO FIX IT
				docEntries.sortedByDescending(DocEntry::date).forEach { entry ->
					appendLine(
						"""
						|    DocArticle("/docs/${
							entry.file.path.substringBeforeLast(".md")
								.replace(Regex(" |_"), "-")
								.replace("\\", "/")
								.substringAfter("doc/")
								.lowercase()
						}",
						|       "${entry.date}",
						|       "${entry.title.escapeQuotes()}",
						|       "${entry.desc.escapeQuotes()}",
						|       "${entry.navTitle.escapeQuotes()}",
						|       ${entry.keywords.asCode()},
						|       "${entry.dateModified}",
						|       ${entry.slugs.asCode()},
						|       ${entry.position ?: "null"}
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

// Task to fetch GitHub releases and generate Kotlin file
// Optional: Add GitHub token if available in environment variables

// Check if we should continue to the next page

// Generate Kotlin file with releases data
// Fetch all pages of releases
tasks.register("fetchGitHubReleases") {
	group = "kore"
	description = "Fetches GitHub releases and generates a Kotlin file with the data"

	val projectDir = projectDir

	doLast {
		val allReleases = mutableListOf<Map<*, *>>()
		var page = 1
		var hasMorePages = true

		// Fetch all pages of releases
		while (hasMorePages) {
			val apiUrl = "https://api.github.com/repos/Ayfri/Kore/releases?per_page=100&page=$page"
			val connection = URI(apiUrl).toURL().openConnection() as HttpURLConnection
			connection.requestMethod = "GET"
			connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

			// Optional: Add GitHub token if available in environment variables
			val githubToken = System.getenv("GITHUB_TOKEN")
			if (githubToken != null && githubToken.isNotBlank()) {
				connection.setRequestProperty("Authorization", "token $githubToken")
			}

			val responseCode = connection.responseCode
			if (responseCode != 200) {
				logger.error("Failed to fetch GitHub releases page $page. Response code: $responseCode")
				break
			}

			val inputStream = connection.inputStream
			val jsonResponse = inputStream.bufferedReader().use { it.readText() }
			inputStream.close()

			val jsonSlurper = JsonSlurper()
			val pageReleases = jsonSlurper.parseText(jsonResponse) as List<Map<*, *>>

			allReleases.addAll(pageReleases)

			// Check if we should continue to the next page
			hasMorePages = pageReleases.isNotEmpty() && pageReleases.size == 100
			page++
		}

		// Generate Kotlin file with releases data
		val outputDir = File(projectDir, "build/generated/kore/src/jsMain/kotlin/io/github/ayfri/kore/website")
		outputDir.mkdirs()

		val outputFile = File(outputDir, "gitHubReleases.kt")
		outputFile.writeText(buildString {
			appendLine("// This file is generated. Do not modify directly.")
			appendLine("")
			appendLine("package io.github.ayfri.kore.website")
			appendLine("")
			appendLine("import io.github.ayfri.kore.website.components.updates.GitHubAsset")
			appendLine("import io.github.ayfri.kore.website.components.updates.GitHubRelease")
			appendLine("")
			appendLine("val gitHubReleases = listOf(")

			allReleases.forEach { release ->
				val id = release["id"] as Number
				val name = (release["name"] as String).replace("\"", "\\\"")
				val tagName = release["tag_name"] as String
				val htmlUrl = release["html_url"] as String
				val url = release["url"] as String
				val createdAt = release["created_at"] as String
				val publishedAt = release["published_at"] as String
				val body = (release["body"] as String).replace("\"\"\"", "\\\"\\\"\\\"").replace("$", "\\$")
				val isPrerelease = release["prerelease"] as Boolean

				val assets = release["assets"] as List<Map<*, *>>

				appendLine("    GitHubRelease(")
				appendLine("        id = $id,")
				appendLine("        name = \"$name\",")
				appendLine("        tagName = \"$tagName\",")
				appendLine("        htmlUrl = \"$htmlUrl\",")
				appendLine("        url = \"$url\",")
				appendLine("        createdAt = \"$createdAt\",")
				appendLine("        publishedAt = \"$publishedAt\",")
				appendLine("        body = \"\"\"$body\"\"\",")
				appendLine("        isPrerelease = $isPrerelease,")

				if (assets.isNotEmpty()) {
					appendLine("        assets = listOf(")
					assets.forEach { asset ->
						val assetId = asset["id"] as Number
						val assetName = (asset["name"] as String).replace("\"", "\\\"")
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

			appendLine(")")
		})

		logger.lifecycle("Generated GitHub releases file with ${allReleases.size} releases")
	}
}

// Make the kobwebExport task depend on fetchGitHubReleases
tasks.named("kobwebExport") {
	dependsOn("fetchGitHubReleases")
}

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


kotlin {
	configAsKobwebApplication("website")

	js {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}

		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			target = "es2015"
		}
		useEsModules()
		binaries.executable()
	}

	sourceSets {
		jsMain {
			kotlin.srcDir("build/generated/kore/src/jsMain/kotlin")
		}
		commonMain {
			dependencies {
				implementation(libs.compose.html.core)
				implementation(libs.compose.runtime)
				implementation(libs.kobweb.core)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobwebx.silk.icons.mdi)
				implementation(npm("marked", libs.versions.marked.get()))
			}
		}
	}
}
