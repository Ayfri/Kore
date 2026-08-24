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

	data class Ready(val files: List<GeneratedFile>, val compileMs: Int, val runMs: Int, val cached: Boolean = false) : OutputState
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

/** Bytes as the visitor reads them, so a 12 KB pack does not show up as five digits. */
private fun humanSize(bytes: Int) = when {
	bytes < 1024 -> "$bytes bytes"
	else -> "${(bytes / 102.4).toInt() / 10.0} KB"
}

@Composable
fun OutputPanel(
	state: OutputState,
	backendConfigured: Boolean,
	maximized: Boolean,
	onToggleMaximize: () -> Unit,
) {
	val scope = rememberCoroutineScope()

	var copied by remember { mutableStateOf(false) }
	var pretty by remember { mutableStateOf(PlaygroundStorage.prettyJson) }
	var selectedPath by remember { mutableStateOf<String?>(null) }

	val files = (state as? OutputState.Ready)?.files.orEmpty()
	val selected = files.firstOrNull { it.path == selectedPath } ?: files.firstOrNull()

	LaunchedEffect(files) {
		selectedPath = files.firstOrNull()?.path
	}

	LaunchedEffect(selected?.path, pretty, maximized) {
		selected ?: return@LaunchedEffect
		initMCFunctionHighlighting()
		document.getElementById(PREVIEW_ID)?.let { Prism.highlightAllUnder(it) }
	}

	Div({ classes(*paneClasses(maximized)) }) {
		Div({ classes(PlaygroundStyle.paneHeader) }) {
			Span({ classes(PlaygroundStyle.paneTitle) }) {
				when (val file = selected) {
					null -> Text("Output")

					else -> {
						if (file.directory.isNotEmpty()) {
							Span({ classes(PlaygroundStyle.pathPrefix) }) { Text("${file.directory}/") }
						}

						Text(file.name)
					}
				}
			}

			Div({ classes(PlaygroundStyle.paneActions) }) {
				if (state is OutputState.Ready) {
					Button({
						classes(*prettyToggleClasses(pretty))
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
						Text(".zip")
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

		when (state) {
			is OutputState.Ready -> {
				Div({ classes(PlaygroundStyle.outputBody) }) {
					FileTree(state.files, selected?.path) { selectedPath = it }

					Div({
						classes(PlaygroundStyle.preview)
						id(PREVIEW_ID)
					}) {
						selected?.let { file ->
							// Prism rewrites the code element's children, detaching the text node Compose owns, so the
							// subtree is rebuilt from scratch on every switch instead of patched in place.
							key(file.path, pretty) {
								CodeBlock(previewOf(file, pretty), grammarOf(file))
							}
						}
					}
				}

				Div({ classes(PlaygroundStyle.statusStrip) }) {
					Span { Text("${state.files.size} files") }
					Span { Text(humanSize(state.files.sumOf { it.content.length })) }
					Span { Text(if (state.cached) "reused a cached compile" else "compiled in ${state.compileMs / 1000.0}s") }
					Span { Text("ran in ${state.runMs}ms") }
				}
			}

			is OutputState.Working -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Div({ classes(PlaygroundStyle.spinner) })
				Span({ classes(PlaygroundStyle.stateTitle) }) { Text(state.label) }
				state.hint?.let { hint -> Span({ classes(PlaygroundStyle.stateDetail) }) { Text(hint) } }

				Div({ classes(PlaygroundStyle.progressTrack) }) {
					Div({
						classes(*progressBarClasses(state.fraction != null))
						state.fraction?.let { fraction -> style { width((fraction * 100).percent) } }
					})
				}
			}

			is OutputState.Failed -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Span({ classes(PlaygroundStyle.stateTitle) }) { Text(state.title) }
				state.detail?.let { detail -> Div({ classes(PlaygroundStyle.errorText) }) { Text(detail) } }
			}

			OutputState.Idle -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Span({ classes(PlaygroundStyle.stateTitle) }) {
					Text(if (backendConfigured) "Nothing generated yet" else "Compiling is unavailable here")
				}

				Span({ classes(PlaygroundStyle.stateDetail) }) {
					Text(
						when {
							backendConfigured -> "Run the snippet to build the datapack. The generated files show up here, ready to preview and download."
							else -> "This deployment has no compile backend configured, so snippets cannot be built. The editor, the examples and shared links still work."
						}
					)
				}
			}
		}
	}
}

private fun paneClasses(maximized: Boolean) = when {
	maximized -> arrayOf(PlaygroundStyle.pane, PlaygroundStyle.paneMaximized)
	else -> arrayOf(PlaygroundStyle.pane)
}

private fun progressBarClasses(determinate: Boolean) = when {
	determinate -> arrayOf(PlaygroundStyle.progressBar)
	else -> arrayOf(PlaygroundStyle.progressBar, PlaygroundStyle.progressBarPending)
}

private fun prettyToggleClasses(pretty: Boolean) = when {
	pretty -> arrayOf(PlaygroundStyle.iconButton, PlaygroundStyle.iconButtonActive)
	else -> arrayOf(PlaygroundStyle.iconButton)
}
