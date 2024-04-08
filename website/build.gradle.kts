import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.children
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.commonmark.node.Text
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe

plugins {
	kotlin("multiplatform")
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
	}
}

class MarkdownVisitor : AbstractVisitor() {
	private val _frontMatter = mutableMapOf<String, List<String>>()
	val frontMatter: Map<String, List<String>> = _frontMatter

	override fun visit(customBlock: CustomBlock) {
		if (customBlock is YamlFrontMatterBlock) {
			val yamlVisitor = YamlFrontMatterVisitor()
			customBlock.accept(yamlVisitor)
			_frontMatter.putAll(
				yamlVisitor.data
					.mapValues { (_, values) ->
						values.map { it.yamlStringToKotlinString() }
					}
			)
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

val generateDocSourceTask = task("generateDocSource") {
	group = "io/github/ayfri/kore/website"
	docInputDir.asFile.mkdirs()

	inputs.dir(docInputDir)
		.withPropertyName("docArticles")
		.withPathSensitivity(PathSensitivity.RELATIVE)
	outputs.dir(docGenDir)
		.withPropertyName("docGeneratedSource")

	val parser = kobweb.markdown.features.createParser()
	val docEntries = mutableListOf<DocEntry>()

	docInputDir.asFileTree.forEach { docArticle ->
		val rootNode = parser.parse(docArticle.readText())
		val visitor = MarkdownVisitor()

		rootNode.accept(visitor)

		val fm = visitor.frontMatter
		val requiredFields = listOf("title", "description", "date-created", "date-modified", "nav-title", "routeOverride")
		val title = fm["title"]?.firstOrNull()
		val desc = fm["description"]?.firstOrNull()
		val dateCreated = fm["date-created"]?.firstOrNull()
		val dateModified = fm["date-modified"]?.firstOrNull()
		val navTitle = fm["nav-title"]?.firstOrNull()
		val routeOverride = fm["routeOverride"]?.firstOrNull()

		if (title == null || desc == null || dateCreated == null || dateModified == null || navTitle == null || routeOverride == null) {
			logger.warn("Skipping '${docArticle.name}', missing required fields in front matter of ${docArticle.name}: ${requiredFields.filter { fm[it] == null }}")
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
			file = docArticle.relativeTo(docInputDir.asFile),
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

	docGenDir.file("$group/docEntries.kt").asFile.apply {
		parentFile.mkdirs()
		writeText(buildString {
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

			docEntries.sortedByDescending { it.date }.forEach { entry ->
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
		})

		logger.info("Generated $absolutePath")
	}
}

kotlin {
	configAsKobwebApplication("website")

	js(IR) {
		browser {
			commonWebpackConfig {
				devServer?.open = false
			}
		}

		binaries.executable()
	}

	sourceSets {
		jsMain {
			kotlin.srcDir(generateDocSourceTask)

			dependencies {
				implementation(compose.html.core)
				implementation(libs.kobweb.core)
				implementation(compose.runtime)
				implementation(libs.kobwebx.markdown)
				implementation(libs.kobwebx.silk.icons.mdi)
			}
		}
	}
}
