package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCheck
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCopy
import com.varabyte.kobweb.silk.components.icons.lucide.LucideDownload
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMaximize2
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMinimize2
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.utils.initMCFunctionHighlighting
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val PREVIEW_ID = "playground-preview"

/** Where a shown pack comes from, which is what the status strip tells the visitor. */
sealed interface OutputOrigin {
	/** An untouched example, run on the JVM when the site was built. */
	data object Precomputed : OutputOrigin

	data class Compiled(val compileMs: Int, val runMs: Int, val cached: Boolean) : OutputOrigin
}

sealed interface OutputState {
	/** Nothing has been run yet in this session. */
	data object Idle : OutputState

	/**
	 * A compile or a run is in flight; [label] is what the visitor is waiting on.
	 *
	 * [fraction] drives a determinate bar. It is real progress where the backend or the loader can measure
	 * it, and an elapsed-against-typical guess for the compile itself, which can only be estimated.
	 */
	data class Working(val label: String, val hint: String? = null, val fraction: Double? = null) : OutputState

	data class Failed(val title: String, val detail: String? = null) : OutputState

	/** The pack generated from [code], which may no longer be what the editor holds. */
	data class Ready(val files: List<GeneratedFile>, val code: String, val origin: OutputOrigin) : OutputState
}

/** Picks the Prism grammar from the file extension; unknown extensions stay unhighlighted. */
private fun grammarOf(file: GeneratedFile) = when (file.extension) {
	"json", "mcmeta" -> "json"
	"mcfunction" -> "mcfunction"
	else -> null
}

private fun previewOf(file: GeneratedFile, pretty: Boolean): String {
	if (!pretty || grammarOf(file) != "json") return file.content

	return runCatching { JSON.stringify(JSON.parse<Any>(file.content), null, 2) }.getOrDefault(file.content)
}

/** The file a pack opens on: a function if there is one, else the first resource that is not a tag or `pack.mcmeta`. */
private fun List<GeneratedFile>.firstShowcase() =
	firstOrNull { it.extension == "mcfunction" } ?: firstOrNull { it.path != "pack.mcmeta" && "/tags/" !in it.path } ?: firstOrNull()

/** Bytes as the visitor reads them, so a 12 KB pack does not show up as five digits. */
private fun humanSize(bytes: Int) = when {
	bytes < 1024 -> "$bytes bytes"
	else -> "${(bytes / 102.4).toInt() / 10.0} KB"
}

private fun OutputOrigin.describe() = when (this) {
	OutputOrigin.Precomputed -> listOf("precomputed at build time")
	is OutputOrigin.Compiled -> listOf(
		if (cached) "reused a cached compile" else "compiled in ${compileMs / 100 / 10.0}s",
		"ran in ${runMs}ms",
	)
}

/**
 * The generated pack: a file tree, the selected file through Prism, and the actions on the whole pack.
 *
 * [stale] marks a pack built from an older buffer, and [rebuild] is the background compile of the current one, if any:
 * the old pack stays readable, dimmed, until the new one replaces it.
 */
@Composable
fun OutputPanel(
	state: OutputState,
	backendConfigured: Boolean,
	stale: Boolean,
	rebuild: CompileProgress?,
	maximized: Boolean,
	onToggleMaximize: () -> Unit,
) {
	val scope = rememberCoroutineScope()

	var copied by remember { mutableStateOf(false) }
	var pretty by remember { mutableStateOf(PlaygroundStorage.prettyJson) }
	var selectedPath by remember { mutableStateOf<String?>(null) }

	val files = (state as? OutputState.Ready)?.files.orEmpty()
	val selected = files.firstOrNull { it.path == selectedPath } ?: files.firstShowcase()

	LaunchedEffect(selected?.path, selected?.content, pretty, maximized) {
		selected ?: return@LaunchedEffect
		initMCFunctionHighlighting()
		document.getElementById(PREVIEW_ID)?.let { Prism.highlightAllUnder(it) }
	}

	Div({
		classes(PlaygroundStyle.pane)
		if (maximized) classes(PlaygroundStyle.paneMaximized)
	}) {
		Div({ classes(PlaygroundStyle.paneHeader) }) {
			Div({ classes(PlaygroundStyle.paneHeading) }) {
				Span({ classes(PlaygroundStyle.paneLabel) }) { Text("Kore generates") }

				selected?.let { file ->
					Span({ classes(PlaygroundStyle.paneTitle) }) {
						if (file.directory.isNotEmpty()) Span({ classes(PlaygroundStyle.pathPrefix) }) { Text("${file.directory}/") }
						Text(file.name)
					}
				}
			}

			Div({ classes(PlaygroundStyle.paneActions) }) {
				if (stale) Span({ classes(PlaygroundStyle.staleBadge) }) {
					Span({
						classes(PlaygroundStyle.staleDot)
						if (rebuild != null) classes(PlaygroundStyle.staleDotPulsing)
					})
					Text(if (rebuild != null) "Edited, rebuilding" else "Edited")
				}

				if (state is OutputState.Ready) {
					Button({
						classes(PlaygroundStyle.iconButton)
						if (pretty) classes(PlaygroundStyle.iconButtonActive)
						onClick {
							pretty = !pretty
							PlaygroundStorage.prettyJson = pretty
						}
						title("Pretty-printed JSON, or the minified form Minecraft actually reads")
					}) {
						Text(if (pretty) "Pretty" else "Raw")
					}

					Button({
						classes(PlaygroundStyle.iconButton)
						title("Copy this file")
						onClick {
							val content = selected?.let { previewOf(it, pretty) } ?: return@onClick

							scope.launch {
								runCatching { window.navigator.asDynamic().clipboard.writeText(content) }
								copied = true
								delay(1500)
								copied = false
							}
						}
					}) {
						if (copied) LucideCheck() else LucideCopy()
					}

					Button({
						classes(PlaygroundStyle.iconButton)
						title("Download the datapack")
						onClick { downloadZip(state.files) }
					}) {
						LucideDownload()
						Span({ classes(PlaygroundStyle.wideOnly) }) { Text(".zip") }
					}
				}

				Button({
					classes(PlaygroundStyle.iconButton)
					title(if (maximized) "Exit fullscreen" else "Open fullscreen")
					onClick { onToggleMaximize() }
				}) {
					if (maximized) LucideMinimize2() else LucideMaximize2()
				}
			}
		}

		rebuild?.let { progress ->
			Div({ classes(PlaygroundStyle.rebuildTrack) }) {
				ProgressBar(progress.fraction)
			}
		}

		when (state) {
			is OutputState.Ready -> {
				Div({
					classes(PlaygroundStyle.outputBody)
					if (stale) classes(PlaygroundStyle.staleBody)
				}) {
					FileTree(state.files, selected?.path) { selectedPath = it }

					Div({
						classes(PlaygroundStyle.preview)
						id(PREVIEW_ID)
					}) {
						selected?.let { file ->
							// Prism rewrites the code element's children, detaching the text node Compose owns, so the
							// subtree is rebuilt from scratch on every switch instead of patched in place.
							key(file.path, file.content, pretty) {
								CodeBlock(previewOf(file, pretty), grammarOf(file))
							}
						}
					}
				}

				Div({ classes(PlaygroundStyle.statusStrip) }) {
					Span { Text("${state.files.size} files") }
					Span { Text(humanSize(state.files.sumOf { it.content.length })) }
					state.origin.describe().forEach { Span { Text(it) } }
					rebuild?.let { Span({ classes(PlaygroundStyle.statusAccent) }) { Text(it.label) } }
				}
			}

			is OutputState.Working -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Div({ classes(PlaygroundStyle.spinner) })
				Span({ classes(PlaygroundStyle.stateTitle) }) { Text(state.label) }
				state.hint?.let { hint -> Span({ classes(PlaygroundStyle.stateDetail) }) { Text(hint) } }

				Div({ classes(PlaygroundStyle.progressTrack) }) {
					ProgressBar(state.fraction)
				}
			}

			is OutputState.Failed -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Span({ classes(PlaygroundStyle.stateTitle) }) { Text(state.title) }
				state.detail?.let { detail -> Div({ classes(PlaygroundStyle.errorText) }) { Text(detail) } }
			}

			OutputState.Idle -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Span({ classes(PlaygroundStyle.stateTitle) }) {
					Text(if (backendConfigured) "Nothing generated yet" else "Only the examples run here")
				}

				Span({ classes(PlaygroundStyle.stateDetail) }) {
					Text(
						when {
							backendConfigured -> "Press Run, or stop typing for a moment: the pack rebuilds on its own and shows up here, ready to preview and download."
							else -> "This deployment has no compile backend, so edited code cannot be built. Every example still shows its generated pack, and editing and sharing work."
						}
					)
				}
			}
		}
	}
}

/** A determinate bar for a known [fraction], a sweeping one otherwise. */
@Composable
private fun ProgressBar(fraction: Double?) = Div({
	classes(PlaygroundStyle.progressBar)
	if (fraction == null) classes(PlaygroundStyle.progressBarPending)
	else style { width((fraction * 100).percent) }
})
