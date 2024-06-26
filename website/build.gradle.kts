import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe
import org.commonmark.node.Text

plugins {
	kotlin("multiplatform")
	kotlin("plugin.compose")
	alias(libs.plugins.jetbrains.compose)
	alias(libs.plugins.kobweb.application)
	alias(libs.plugins.kobwebx.markdown)
}

group = "io.github.ayfri.kore.website"
version = "1.0-SNAPSHOT"

val docInputDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/doc")
val docGenDir = layout.buildDirectory.dir("generated/ayfri/src/jsMain/kotlin").get()

kobweb {
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
					logger.warn("Skipping '$fileName', missing required fields in front matter of $fileName: ${requiredFields.filter { fm[it] == null }}")
					return@forEach
				}

				val keywords = fm["keywords"]?.firstOrNull()?.split(Regex(",\\s*")) ?: emptyList()
				// Dates are only formatted in this format "2023-11-13"
				val dateCreatedComplete = dateCreated.split("-").let { (year, month, day) ->
					"$year-$month-${day}T00:00:00.000000000+01:00"
				}
				val dateModifiedComplete = dateModified.split("-").let { (year, month, day) ->
					"$year-$month-${day}T00:00:00.000000000+01:00"
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
					slugs = slugs
				)
				docEntries += newEntry
			}

			generateKotlin("$group/docEntries.kt", buildString {
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

				docEntries.sortedByDescending(DocEntry::date).forEach { entry ->
					appendLine(
						"""
					|    DocArticle("/docs/${
							entry.file.path.substringBeforeLast(".md")
								.replace(Regex(" |_"), "-")
								.replace("\\", "/")
								.lowercase()
						}",
					|       "${entry.date}",
					|       "${entry.title.escapeQuotes()}",
					|       "${entry.desc.escapeQuotes()}",
					|       "${entry.navTitle.escapeQuotes()}",
					|       ${entry.keywords.asCode()},
					|       "${entry.dateModified}",
					|       ${entry.slugs.asCode()}
					|   ),
					""".trimMargin()
					)
					logger.info("Generated entry for ${entry.file.name}")
				}

				appendLine(")")
				logger.info("Generated ${docEntries.size} entries in docEntries.kt")
			})
		}
	}
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
)

fun String.escapeQuotes() = this.replace("\"", "\\\"")

kotlin {
	configAsKobwebApplication("website")

	js {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}

		binaries.executable()
	}

	sourceSets {
		commonMain {
//			kotlin.srcDir(generateDocSourceTask)

			dependencies {
				implementation(compose.html.core)
				implementation(compose.runtime)
				implementation(libs.kobweb.core)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobwebx.silk.icons.mdi)
			}
		}
	}
}
