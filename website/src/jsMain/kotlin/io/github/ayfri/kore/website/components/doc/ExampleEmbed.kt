package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.components.common.Tab
import io.github.ayfri.kore.website.components.common.Tabs
import io.github.ayfri.kore.website.components.playground.GeneratedFile
import io.github.ayfri.kore.website.components.playground.firstShowcase
import io.github.ayfri.kore.website.components.playground.grammarOf
import io.github.ayfri.kore.website.components.playground.playgroundExamples
import io.github.ayfri.kore.website.components.playground.precomputedFiles
import io.github.ayfri.kore.website.components.playground.previewOf
import io.github.ayfri.kore.website.utils.highlightCodeIn
import io.github.ayfri.kore.website.utils.monoFont
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

/**
 * The playground example [slug] on a doc page: its Kotlin and the files it generates, both read from build-time data.
 *
 * The files are fetched by the browser, so the exported HTML always holds the Kotlin and a loading line for the files.
 */
@Composable
internal fun ExampleEmbed(slug: String) {
	val example = playgroundExamples.first { it.slug == slug }
	/** The imports stay in the playground: on a doc page they push the example itself a screen down. */
	val code = remember(slug) { example.code.lineSequence().dropWhile { it.startsWith("import ") || it.isBlank() }.joinToString("\n") }
	var files by remember { mutableStateOf<List<GeneratedFile>?>(null) }
	var failed by remember { mutableStateOf(false) }
	val kotlinId = "example-$slug-kotlin"

	LaunchedEffect(slug) {
		val loaded = example.precomputedFiles()
		files = loaded
		failed = loaded == null
	}

	Div({ classes(ExampleEmbedStyle.embed) }) {
		Div({ classes(ExampleEmbedStyle.header) }) {
			Span({ classes(ExampleEmbedStyle.title) }) { Text(example.title) }
			Span({ classes(ExampleEmbedStyle.description) }) { Text(example.description) }
			PlaygroundLink(slug)
		}

		Tabs(
			listOf(
				Tab("Kotlin") { Div({ id(kotlinId) }) { CodeBlock(code, "kotlin") } },
				Tab("Generated files") { GeneratedFiles(slug, files, failed) },
			),
		)

		highlightCodeIn(kotlinId, code)
	}
}

@Composable
private fun PlaygroundLink(slug: String) = A("/playground#example=$slug") { Text("Open in the playground") }

/** One pill per generated file except `pack.mcmeta`, which every pack has, opening on the file [firstShowcase] picks. */
@Composable
private fun GeneratedFiles(slug: String, files: List<GeneratedFile>?, failed: Boolean) {
	var selectedPath by remember { mutableStateOf<String?>(null) }
	val fileId = "example-$slug-file"

	Div({ classes(ExampleEmbedStyle.files) }) {
		when {
			failed -> Div({ classes(ExampleEmbedStyle.description) }) {
				Text("The generated files could not be loaded. ")
				PlaygroundLink(slug)
			}

			files == null -> Div({ classes(ExampleEmbedStyle.description) }) { Text("Loading the files Kore generates for this example.") }

			else -> {
				val shown = files.filter { it.path != "pack.mcmeta" }
				val selected = shown.firstOrNull { it.path == selectedPath } ?: shown.firstShowcase() ?: return@Div
				val duplicated = shown.groupBy { it.name }.filterValues { it.size > 1 }.keys

				PillGroup(
					"Generated file",
					shown,
					selected,
					{ if (it.name in duplicated) it.path.split('/').takeLast(2).joinToString("/") else it.name },
				) { selectedPath = it.path }

				Div({ classes(ExampleEmbedStyle.path) }) { Text(selected.path) }

				Div({ id(fileId) }) {
					/** Prism rewrites the code element's children, detaching the text node Compose owns, so the block is rebuilt on every switch. */
					key(selected.path) { CodeBlock(previewOf(selected, 2), grammarOf(selected)) }
				}

				highlightCodeIn(fileId, selected.path)
			}
		}
	}
}

object ExampleEmbedStyle : StyleSheet() {
	val description by style {
		color(LandingVars.Muted.value())
		fontSize(0.85.cssRem)
	}

	val embed by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		margin(1.5.cssRem, 0.px)
		minWidth(0.px)
	}

	val files by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		minWidth(0.px)
		padding(0.75.cssRem)
	}

	val header by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.3.cssRem, 1.cssRem)
	}

	val path by style {
		color(LandingVars.Muted.value())
		fontSize(0.75.cssRem)
		monoFont()
		overflowWrap(OverflowWrap.Anywhere)
	}

	val title by style {
		color(LandingVars.Text.value())
		fontWeight(600)
	}
}
