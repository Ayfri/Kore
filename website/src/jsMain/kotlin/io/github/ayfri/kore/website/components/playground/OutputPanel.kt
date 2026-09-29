package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.utils.initMCFunctionHighlighting
import kotlinx.browser.document
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*

private const val PREVIEW_ID = "playground-preview"

/** Where a shown pack comes from, which is what the status bar tells the visitor. */
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

/** The file as the preview shows it, its JSON re-indented by [indent] spaces, or left as generated when [indent] is null. */
private fun previewOf(file: GeneratedFile, indent: Int?): String {
	if (indent == null || grammarOf(file) != "json") return file.content

	return runCatching { JSON.stringify(JSON.parse<Any>(file.content), null, indent) }.getOrDefault(file.content)
}

/** The file a pack opens on: a function if there is one, else the first resource that is not a tag or `pack.mcmeta`. */
private fun List<GeneratedFile>.firstShowcase() =
	firstOrNull { it.extension == "mcfunction" } ?: firstOrNull { it.path != "pack.mcmeta" && "/tags/" !in it.path } ?: firstOrNull()

/** Bytes as the visitor reads them, so a 12 KB pack does not show up as five digits. */
fun humanSize(bytes: Int) = when {
	bytes < 1024 -> "$bytes B"
	else -> "${(bytes / 102.4).toInt() / 10.0} KB"
}

fun OutputOrigin.describe() = when (this) {
	OutputOrigin.Precomputed -> "precomputed at build time"
	is OutputOrigin.Compiled -> if (cached) "reused a cached compile" else "compiled in ${compileMs / 100 / 10.0}s, ran in ${runMs}ms"
}

/**
 * The generated pack: a filterable file tree, the selected file through Prism, and the actions on files and the pack.
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
	onCopy: (text: String, what: String) -> Unit,
	onShowProblems: () -> Unit,
) {
	var filter by remember { mutableStateOf("") }
	var selectedPath by remember { mutableStateOf<String?>(null) }

	val maximized = PlaygroundLayout.maximizedPane == MaximizedPane.OUTPUT
	val indent = PlaygroundSettings.jsonIndent.takeIf { PlaygroundSettings.prettyJson }
	val wrap = PlaygroundSettings.previewWrap
	val files = (state as? OutputState.Ready)?.files.orEmpty()
	val needle = filter.trim().lowercase()
	val shown = remember(files, needle) { files.filter { needle in it.path.lowercase() } }
	val nodes = remember(shown) { buildFileTree(shown) }
	val collapsed = remember(files) { mutableStateMapOf<String, Boolean>() }
	val selected = files.firstOrNull { it.path == selectedPath } ?: files.firstShowcase()

	LaunchedEffect(selected?.path, selected?.content, indent, wrap, maximized) {
		selected ?: return@LaunchedEffect
		initMCFunctionHighlighting()
		document.getElementById(PREVIEW_ID)?.let { Prism.highlightAllUnder(it) }
	}

	Div({
		classes(PlaygroundStyle.pane)
		if (maximized) classes(PlaygroundStyle.paneMaximized)
	}) {
		Div({ classes(PlaygroundStyle.tabStrip) }) {
			Div({ classes(PlaygroundStyle.tab, PlaygroundStyle.tabActive) }) {
				LucidePackage()
				Text("Datapack")
				if (state is OutputState.Ready) Span({ classes(PlaygroundStyle.tabMeta) }) { Text("${files.size} files") }
			}

			Div({ classes(PlaygroundStyle.tabActions) }) {
				if (stale) Span({ classes(PlaygroundStyle.staleBadge) }) {
					Span({
						classes(PlaygroundStyle.staleDot)
						if (rebuild != null) classes(PlaygroundStyle.staleDotPulsing)
					})

					Text(
						when {
							rebuild != null -> "Rebuilding"
							PlaygroundSettings.autoBuild -> "Edited"
							else -> "Edited, press Run"
						}
					)
				}

				ToolButton(
					"Toggle the file tree",
					{ PlaygroundLayout.explorerOpen = !PlaygroundLayout.explorerOpen },
					active = PlaygroundLayout.explorerOpen,
				) {
					LucidePanelLeft()
				}

				ToolButton(
					"Download the datapack",
					{ downloadZip(files) },
					enabled = state is OutputState.Ready,
					keys = PlaygroundCommand.DOWNLOAD_ZIP.keys,
					classes = arrayOf(PlaygroundStyle.toolButtonLabelled),
				) {
					LucideDownload()
					Span({ classes(PlaygroundStyle.wideOnly) }) { Text(".zip") }
				}

				ToolButton(
					if (maximized) "Exit fullscreen" else "Open fullscreen",
					{ PlaygroundLayout.maximizedPane = PlaygroundLayout.maximizedPane.toggle(MaximizedPane.OUTPUT) },
					active = maximized,
				) {
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
			is OutputState.Ready -> Div({
				classes(PlaygroundStyle.outputBody)
				if (!PlaygroundLayout.explorerOpen) classes(PlaygroundStyle.outputBodyPreviewOnly)
				if (stale) classes(PlaygroundStyle.staleBody)
			}) {
				if (PlaygroundLayout.explorerOpen) Div({ classes(PlaygroundStyle.explorer) }) {
					Div({ classes(PlaygroundStyle.sectionHeader) }) {
						Span({ classes(PlaygroundStyle.sectionTitle) }) {
							Text("Files")
							Span({ classes(PlaygroundStyle.treeCount) }) { Text(if (needle.isEmpty()) "${files.size}" else "${shown.size}/${files.size}") }
						}

						Span({ classes(PlaygroundStyle.sectionActions) }) {
							ToolButton("Collapse all", { nodes.folderPaths().forEach { collapsed[it] = true } }) { LucideChevronsDownUp() }
							ToolButton("Expand all", { collapsed.clear() }) { LucideChevronsUpDown() }
						}
					}

					Div({ classes(PlaygroundStyle.searchBox, PlaygroundStyle.searchBoxCompact) }) {
						LucideListFilter()
						SearchInput(filter) {
							classes(PlaygroundStyle.searchInput)
							attr("placeholder", "Filter files")
							attr("aria-label", "Filter the generated files")
							onInput { filter = it.value }
						}
					}

					FileTree(nodes, selected?.path, collapsed) { selectedPath = it }
					if (shown.isEmpty()) Div({ classes(PlaygroundStyle.sideEmpty) }) { Text("No file matches \"$filter\".") }
				}

				Div({ classes(PlaygroundStyle.previewColumn) }) {
					selected?.let { file -> PreviewHeader(file, indent, wrap, onCopy) }

					Div({
						classes(PlaygroundStyle.preview)
						if (wrap) classes(PlaygroundStyle.previewWrapped)
						id(PREVIEW_ID)
					}) {
						selected?.let { file ->
							// Prism rewrites the code element's children, detaching the text node Compose owns, so the
							// subtree is rebuilt from scratch on every switch instead of patched in place.
							key(file.path, file.content, indent, wrap) {
								CodeBlock(previewOf(file, indent), grammarOf(file), "line-numbers")
							}
						}
					}
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
				Span({ classes(PlaygroundStyle.stateIcon, PlaygroundStyle.stateIconError) }) { LucideCircleX() }
				Span({ classes(PlaygroundStyle.stateTitle) }) { Text(state.title) }
				state.detail?.let { detail -> Div({ classes(PlaygroundStyle.errorText) }) { Text(detail) } }

				Button({
					classes(PlaygroundStyle.textButton)
					onClick { onShowProblems() }
				}) {
					LucideCircleAlert()
					Text("Show the problems and the build log")
				}
			}

			OutputState.Idle -> Div({ classes(PlaygroundStyle.stateBox) }) {
				Span({ classes(PlaygroundStyle.stateIcon) }) { LucidePackage() }

				Span({ classes(PlaygroundStyle.stateTitle) }) {
					Text(if (backendConfigured) "Nothing generated yet" else "Only the examples run here")
				}

				Span({ classes(PlaygroundStyle.stateDetail) }) {
					Text(
						when {
							backendConfigured -> "Press Run, or stop typing for a moment with live rebuild on: the pack shows up here, ready to browse and download."
							else -> "This deployment has no compile backend, so edited code cannot be built. Every example still shows its generated pack, and editing and sharing work."
						}
					)
				}

				if (backendConfigured) Span({ classes(PlaygroundStyle.stateDetail) }) { Keys(PlaygroundCommand.RUN.keys.orEmpty()) }
			}
		}
	}
}

/** The selected file's name then its folder, which copies the path when clicked, and the actions on that one file. */
@Composable
private fun PreviewHeader(file: GeneratedFile, indent: Int?, wrap: Boolean, onCopy: (text: String, what: String) -> Unit) {
	val pretty = indent != null

	Div({ classes(PlaygroundStyle.previewHeader) }) {
		Button({
			classes(PlaygroundStyle.breadcrumb)
			title("${file.path} · ${humanSize(file.content.length)}\nClick to copy the path")
			onClick { onCopy(file.path, "Path") }
		}) {
			Span({ classes(PlaygroundStyle.breadcrumbFile) }) {
				FileIcon(file)
				Span({ classes(PlaygroundStyle.breadcrumbFileName) }) { Text(file.name) }
			}

			if (file.directory.isNotEmpty()) Span({ classes(PlaygroundStyle.breadcrumbDirectory) }) { Text(file.directory) }
		}

		Div({ classes(PlaygroundStyle.sectionActions) }) {
			if (grammarOf(file) == "json") ToolButton(
				if (pretty) "Pretty-printed, switch to the minified JSON Minecraft reads" else "Minified, switch to pretty-printed JSON",
				{ PlaygroundSettings.prettyJson = !pretty },
				active = pretty,
				classes = arrayOf(PlaygroundStyle.toolButtonLabelled),
			) {
				LucideBraces()
				Span({ classes(PlaygroundStyle.wideOnly) }) { Text(if (pretty) "Pretty" else "Raw") }
			}

			ToolButton("Wrap long lines", { PlaygroundSettings.previewWrap = !wrap }, active = wrap) { LucideTextWrap() }
			ToolButton("Copy the content", { onCopy(previewOf(file, indent), file.name) }) { LucideCopy() }
			ToolButton("Download this file", { downloadText(previewOf(file, indent), file.name) }) { LucideFileDown() }
		}
	}
}

/** A determinate bar for a known [fraction], a sweeping one otherwise. */
@Composable
fun ProgressBar(fraction: Double?) = Div({
	classes(PlaygroundStyle.progressBar)
	if (fraction == null) classes(PlaygroundStyle.progressBarPending)
	else style { width((fraction * 100).percent) }
})
