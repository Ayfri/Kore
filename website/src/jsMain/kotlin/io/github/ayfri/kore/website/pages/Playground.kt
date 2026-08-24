package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMaximize2
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMinimize2
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.playground.*
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.utils.onEvents
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

private const val COMPILE_HINT = "Kotlin/JS compilation against Kore takes about 30 seconds. The result is cached, so re-running an unchanged snippet is instant."

@Page
@Composable
fun PlaygroundPage() {
	Style(PlaygroundStyle)

	setDescription("Write Kotlin, get a Minecraft datapack. Try the Kore DSL in your browser, no install needed.")
	setKeywords(
		"kore playground", "kotlin datapack editor", "minecraft datapack generator online",
		"try kore", "datapack builder", "kotlin dsl playground"
	)

	val scope = rememberCoroutineScope()
	val backendConfigured = remember { playgroundApiUrl != null }

	var busy by remember { mutableStateOf(false) }
	var code by remember { mutableStateOf(defaultExample.code) }
	var diagnostics by remember { mutableStateOf(emptyList<PlaygroundDiagnostic>()) }
	var editor by remember { mutableStateOf<CodeEditor?>(null) }
	var maximizedPane by remember { mutableStateOf(MaximizedPane.NONE) }
	var resizing by remember { mutableStateOf(false) }
	var splitFraction by remember { mutableStateOf(DEFAULT_SPLIT) }
	var workspace by remember { mutableStateOf<HTMLElement?>(null) }
	var elapsedSeconds by remember { mutableStateOf(0) }
	var initialCode by remember { mutableStateOf<String?>(null) }
	var output by remember { mutableStateOf<OutputState>(OutputState.Idle) }
	var selectedExample by remember { mutableStateOf<PlaygroundExample?>(defaultExample) }
	var shareLabel by remember { mutableStateOf("Share") }

	// A shared link wins over the default example, and must be resolved before the editor is created.
	LaunchedEffect(Unit) {
		val shared = sharedCode()
		if (shared != null) selectedExample = null
		code = shared ?: defaultExample.code
		initialCode = code
	}

	LaunchedEffect(busy) {
		elapsedSeconds = 0
		while (busy) {
			delay(1000)
			elapsedSeconds++
		}
	}

	fun run() {
		if (busy || !backendConfigured) return

		scope.launch {
			busy = true
			output = OutputState.Working("Compiling", COMPILE_HINT)

			val result = runCatching { compilePlayground(code) }.getOrElse { throwable ->
				busy = false
				output = OutputState.Failed("Could not reach the compile backend", throwable.message)
				return@launch
			}

			diagnostics = result.diagnostics
			editor?.showDiagnostics(result.diagnostics)

			if (!result.succeeded) {
				busy = false
				output = OutputState.Failed(
					title = "Compilation failed",
					detail = result.exception
						?: result.errors.joinToString("\n") { "${it.startLine}:${it.startColumn}  ${it.message}" }
							.ifEmpty { "The compiler returned no code and no message." },
				)
				return@launch
			}

			output = OutputState.Working("Running", "The compiled pack is built in your browser, inside a worker.")

			output = when (val execution = runCompiledPack(result.evaluationOrder)) {
				is RunResult.Success -> OutputState.Ready(execution.files, result.durationMs, execution.durationMs)
				is RunResult.Failure -> OutputState.Failed("The snippet failed while running", execution.message)
			}

			busy = false
		}
	}

	document.onEvents(
		"mousemove" to { event ->
			val element = workspace

			if (resizing && element != null) {
				val rect = element.getBoundingClientRect()
				splitFraction = ((event as MouseEvent).clientX - rect.left) / rect.width
				splitFraction = splitFraction.coerceIn(MIN_SPLIT, MAX_SPLIT)
			}
		},
		"mouseup" to { resizing = false },
		key = resizing,
	)

	val currentRun by rememberUpdatedState(::run)

	DisposableEffect(Unit) {
		val listener = { event: dynamic ->
			val keyboardEvent = event.unsafeCast<KeyboardEvent>()

			if (keyboardEvent.key == "Enter" && (keyboardEvent.ctrlKey || keyboardEvent.metaKey)) {
				keyboardEvent.preventDefault()
				currentRun()
			}

			if (keyboardEvent.key == "Escape") maximizedPane = MaximizedPane.NONE
		}

		document.addEventListener("keydown", listener)
		onDispose { document.removeEventListener("keydown", listener) }
	}

	PageLayout("Playground - Try Kore in your browser") {
		Div({ classes(PlaygroundStyle.container) }) {
			Div({ classes(PlaygroundStyle.hero) }) {
				H1({ classes(PlaygroundStyle.pageTitle) }) { Text("Playground") }

				P({ classes(PlaygroundStyle.description) }) {
					Text("Write Kore Kotlin, run it, and get the generated datapack back. Nothing to install.")
				}
			}

			PlaygroundToolbar(
				selectedExample = selectedExample,
				busy = busy,
				canRun = backendConfigured,
				shareLabel = shareLabel,
				onRun = ::run,
				onSelectExample = { example ->
					selectedExample = example
					code = example.code
					editor?.setValue(example.code)
					diagnostics = emptyList()
					editor?.showDiagnostics(emptyList())
					output = OutputState.Idle
				},
				onReset = {
					val example = selectedExample ?: defaultExample
					selectedExample = example
					code = example.code
					editor?.setValue(example.code)
					diagnostics = emptyList()
					editor?.showDiagnostics(emptyList())
					output = OutputState.Idle
				},
				onShare = {
					scope.launch {
						val url = shareUrl(code)
						window.history.replaceState(null, "", url)
						runCatching { window.navigator.asDynamic().clipboard.writeText(url) }
						shareLabel = "Link copied"
						delay(2000)
						shareLabel = "Share"
					}
				},
			)

			Div({
				classes(PlaygroundStyle.workspace)
				ref {
					workspace = it
					onDispose { workspace = null }
				}

				style {
					// A ratio rather than a column list, so the mobile media query can still collapse to one column.
					property("--playground-split", "${splitFraction / (1 - splitFraction)}fr")
					property("user-select", if (resizing) "none" else "auto")
				}
			}) {
				Div({ classes(*editorPaneClasses(maximizedPane == MaximizedPane.EDITOR)) }) {
					Div({ classes(PlaygroundStyle.paneHeader) }) {
						Span({ classes(PlaygroundStyle.paneTitle) }) { Text("main.kt") }

						Div({ classes(PlaygroundStyle.paneActions) }) {
							Span({ classes(PlaygroundStyle.paneHint) }) { Text("Ctrl+Enter to run") }

							Button({
								classes(PlaygroundStyle.iconButton)
								title(if (maximizedPane == MaximizedPane.EDITOR) "Exit fullscreen" else "Open fullscreen")
								onClick {
									maximizedPane = when (maximizedPane) {
										MaximizedPane.EDITOR -> MaximizedPane.NONE
										else -> MaximizedPane.EDITOR
									}
								}
							}) {
								if (maximizedPane == MaximizedPane.EDITOR) LucideMinimize2() else LucideMaximize2()
							}
						}
					}

					initialCode?.let { value ->
						MonacoEditor(
							initialValue = value,
							className = PlaygroundStyle.editor,
							onChange = {
								code = it
								selectedExample = playgroundExamples.firstOrNull { example -> example.code == it }
							},
							onReady = { editor = it },
						)
					}

					ProblemsStrip(diagnostics) { diagnostic -> editor?.revealDiagnostic(diagnostic) }
				}

				Div({
					classes(*splitterClasses(resizing))
					attr("aria-orientation", "vertical")
					attr("role", "separator")
					title("Drag to resize, double-click to reset")
					onDoubleClick { splitFraction = DEFAULT_SPLIT }
					onMouseDown {
						it.preventDefault()
						resizing = true
					}
				})

				OutputPanel(
					state = output.withElapsed(elapsedSeconds),
					backendConfigured = backendConfigured,
					maximized = maximizedPane == MaximizedPane.OUTPUT,
					onToggleMaximize = {
						maximizedPane = when (maximizedPane) {
							MaximizedPane.OUTPUT -> MaximizedPane.NONE
							else -> MaximizedPane.OUTPUT
						}
					},
				)
			}
		}
	}
}

/** Share of the workspace width given to the editor, and the range the splitter may drag it through. */
private const val DEFAULT_SPLIT = 0.55
private const val MAX_SPLIT = 0.8
private const val MIN_SPLIT = 0.2

private fun splitterClasses(resizing: Boolean) = when {
	resizing -> arrayOf(PlaygroundStyle.splitter, PlaygroundStyle.splitterActive)
	else -> arrayOf(PlaygroundStyle.splitter)
}

/** Which pane, if any, is currently taking over the viewport. */
private enum class MaximizedPane {
	EDITOR,
	NONE,
	OUTPUT,
}

private fun editorPaneClasses(maximized: Boolean) = when {
	maximized -> arrayOf(PlaygroundStyle.pane, PlaygroundStyle.paneMaximized)
	else -> arrayOf(PlaygroundStyle.pane)
}

/** Shows how long the visitor has been waiting, so a 30s compile never looks like a hung spinner. */
private fun OutputState.withElapsed(seconds: Int) = when {
	this is OutputState.Working && seconds > 0 -> copy(label = "$label... ${seconds}s")
	else -> this
}
