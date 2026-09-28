package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.AppGlobals
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

/** Quiet time after the last keystroke before type-checking and compiling what the editor holds. */
private const val IDLE_DELAY_MS = 700L

/** What the wait looks like before the backend has said anything, worded from this visitor's own timings. */
private fun compileHint(typicalMs: Int?) = when (typicalMs) {
	null -> "Compiling Kotlin to JavaScript against Kore. The result is cached, so running an unchanged snippet again is instant."
	else -> "Your last compiles took about ${typicalMs / 1000}s. The result is cached, so running an unchanged snippet again is instant."
}

@Page
@Composable
fun PlaygroundPage() {
	Style(HomePageStyle)
	Style(PlaygroundStyle)

	val scope = rememberCoroutineScope()
	val backendConfigured = remember { playgroundApiUrl != null }

	var busy by remember { mutableStateOf(false) }
	var code by remember { mutableStateOf(defaultExample.code) }
	var diagnostics by remember { mutableStateOf(emptyList<PlaygroundDiagnostic>()) }
	var editor by remember { mutableStateOf<CodeEditor?>(null) }
	var maximizedPane by remember { mutableStateOf(MaximizedPane.NONE) }
	var resizing by remember { mutableStateOf(false) }
	var splitFraction by remember { mutableStateOf(PlaygroundStorage.splitFraction?.coerceIn(MIN_SPLIT, MAX_SPLIT) ?: DEFAULT_SPLIT) }
	var workspace by remember { mutableStateOf<HTMLElement?>(null) }
	var elapsedSeconds by remember { mutableStateOf(0) }
	var typicalCompileMs by remember { mutableStateOf<Int?>(null) }
	var initialCode by remember { mutableStateOf<String?>(null) }
	var output by remember { mutableStateOf<OutputState>(OutputState.Idle) }
	var selectedExample by remember { mutableStateOf<PlaygroundExample?>(defaultExample) }
	var shareLabel by remember { mutableStateOf("Share") }
	var runnerPrewarmed by remember { mutableStateOf(false) }

	fun showDiagnostics(list: List<PlaygroundDiagnostic>) {
		diagnostics = list
		editor?.showDiagnostics(list)
	}

	/** Shows the pack an untouched example generates, computed at build time. False when [target] is not an example. */
	suspend fun showPrecomputed(target: String): Boolean {
		val files = playgroundExamplesByCode[target]?.precomputedFiles() ?: return false
		if (code == target) output = OutputState.Ready(files, target, OutputOrigin.Precomputed)
		return true
	}

	/**
	 * Compiles [target], runs it in a worker and shows the pack, unless the buffer moved on meanwhile.
	 *
	 * A [live] build is one the page started on idle: it keeps the current output on screen instead of a waiting
	 * state, leaves compile errors to the squiggles and the problems strip, and drops a full compile queue
	 * silently, since nobody pressed anything. Only a pack that fails while running replaces the output.
	 */
	suspend fun build(target: String, live: Boolean) {
		val result = try {
			PlaygroundCompiler.compile(target)
		} catch (cancelled: CancellationException) {
			throw cancelled
		} catch (busyBackend: CompileBusyException) {
			if (!live) output = OutputState.Failed("The compile backend is busy", busyBackend.message)
			return
		} catch (failure: Throwable) {
			if (!live) output = OutputState.Failed("Could not reach the compile backend", failure.message)
			return
		}

		if (code != target) return
		showDiagnostics(result.diagnostics)

		if (!result.succeeded) {
			if (live) return
			output = OutputState.Failed(
				title = "Compilation failed",
				detail = result.exception
					?: result.errors.joinToString("\n") { "${it.startLine}:${it.startColumn}  ${it.message}" }
						.ifEmpty { "The compiler returned no code and no message." },
			)
			return
		}

		if (!result.cached) {
			PlaygroundStorage.recordCompile(result.durationMs)
			typicalCompileMs = PlaygroundStorage.typicalCompileMs
		}

		if (!live) output = OutputState.Working("Running", "The compiled pack is built in your browser, inside a worker.")

		val execution = PackRunner.run(result.evaluationOrder) { progress ->
			if (!live) output = OutputState.Working(progress.label, progress.detail, progress.fraction)
		}

		if (code != target) return

		if (execution is RunResult.Success) {
			PlaygroundStorage.libraries = result.evaluationOrder.dropLast(1).mapNotNull { chunk -> chunk.hash?.let { chunk.name to it } }
		}

		output = when (execution) {
			is RunResult.Success -> OutputState.Ready(
				files = execution.files,
				code = target,
				origin = OutputOrigin.Compiled(result.durationMs, execution.durationMs, result.cached),
			)

			is RunResult.Failure -> OutputState.Failed("The snippet failed while running", execution.message)
		}
	}

	fun run() {
		if (busy) return
		val target = code

		scope.launch {
			busy = true

			try {
				when {
					showPrecomputed(target) -> Unit
					!backendConfigured -> output = OutputState.Failed(
						"Compiling is unavailable here",
						"This deployment has no compile backend, so only the examples run as they are.",
					)

					else -> {
						output = OutputState.Working("Compiling", compileHint(typicalCompileMs))
						build(target, live = false)
					}
				}
			} finally {
				busy = false
			}
		}
	}

	// A shared link wins over a restored draft, which wins over the default example. Resolved before the
	// editor is created, since Monaco only reads its initial value once.
	LaunchedEffect(Unit) {
		code = sharedCode() ?: PlaygroundStorage.draft ?: defaultExample.code
		selectedExample = playgroundExamplesByCode[code]
		initialCode = code
		typicalCompileMs = PlaygroundStorage.typicalCompileMs
		showPrecomputed(code)
	}

	// Debounced through the effect itself: a new keystroke cancels the pending write.
	LaunchedEffect(code) {
		if (initialCode == null) return@LaunchedEffect
		delay(400)
		PlaygroundStorage.draft = code
	}

	// Typing back to an untouched example shows its pack again. Otherwise, once typing pauses, the JVM type-check and
	// the real compile start together: the type-check draws squiggles in well under a second, a buffer that does not
	// type-check fails the compile's first phase about as fast, and a clean one is linked and run without waiting for
	// Run, so the pack is usually there before the click. A new keystroke cancels all of it, and a compile already
	// sent still lands in the cache.
	LaunchedEffect(code) {
		if (initialCode == null) return@LaunchedEffect

		if (code in playgroundExamplesByCode) {
			showDiagnostics(emptyList())
			showPrecomputed(code)
			return@LaunchedEffect
		}

		if (!backendConfigured) return@LaunchedEffect

		if (!runnerPrewarmed) {
			runnerPrewarmed = true
			scope.launch { prewarmRunner() }
		}

		delay(IDLE_DELAY_MS)

		val target = code
		launch {
			val fresh = runCatching { highlightPlayground(target) }.getOrNull() ?: return@launch
			// A Run in flight owns the markers: its JS diagnostics are the ones that matter.
			if (!busy) showDiagnostics(fresh)
		}

		build(target, live = true)
	}

	LaunchedEffect(splitFraction) {
		delay(200)
		PlaygroundStorage.splitFraction = splitFraction
	}

	LaunchedEffect(busy) {
		elapsedSeconds = 0
		while (busy) {
			delay(1000)
			elapsedSeconds++
		}
	}

	document.onEvents(
		"mousemove" to { event ->
			val element = workspace

			if (resizing && element != null) {
				val rect = element.getBoundingClientRect()
				splitFraction = (((event as MouseEvent).clientX - rect.left) / rect.width).coerceIn(MIN_SPLIT, MAX_SPLIT)
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

	// A compile of this very buffer, started by the idle path or by Run, is what the output pane reports.
	val compiling = PlaygroundCompiler.running?.takeIf { it.code == code }
	val ready = output as? OutputState.Ready
	val shownOutput = when {
		busy && output is OutputState.Working && compiling != null -> compiling.progress.let {
			OutputState.Working(it.label, it.detail, it.fraction)
		}

		else -> output
	}.withElapsed(elapsedSeconds, typicalCompileMs)

	PageLayout("Playground - Try Kore in your browser") {
		setDescription("Write Kotlin, get a Minecraft datapack. Try the Kore DSL in your browser with instant examples, live rebuilds and a zip download.")
		setKeywords(
			"kore playground", "kotlin datapack editor", "minecraft datapack generator online",
			"try kore", "datapack builder", "kotlin dsl playground"
		)

		Div({ classes(HomePageStyle.page) }) {
			Div({ classes(PlaygroundStyle.container) }) {
				Header({ classes(PlaygroundStyle.header) }) {
					Span({ classes(PlaygroundStyle.eyebrow) }) {
						Text("Kore ${AppGlobals["projectVersion"] ?: "?"} · Minecraft ${AppGlobals["minecraftVersion"] ?: "?"}")
					}
					H1 { Text("Playground") }
					P {
						Text("Write Kore Kotlin and get the generated datapack back, nothing to install. Examples load instantly, and an edit rebuilds on its own once you stop typing.")
					}
				}

				PlaygroundToolbar(
					selectedExample = selectedExample,
					busy = busy,
					canRun = backendConfigured || code in playgroundExamplesByCode,
					shareLabel = shareLabel,
					onRun = ::run,
					onSelectExample = { example ->
						selectedExample = example
						code = example.code
						editor?.setValue(example.code)
					},
					onReset = {
						val example = selectedExample ?: defaultExample
						selectedExample = example
						code = example.code
						editor?.setValue(example.code)
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
						// A ratio rather than a column list, so the narrow-screen media query can still collapse to one column.
						property("--playground-split", "${splitFraction / (1 - splitFraction)}fr")
						property("user-select", if (resizing) "none" else "auto")
					}
				}) {
					Div({
						classes(PlaygroundStyle.pane)
						if (maximizedPane == MaximizedPane.EDITOR) classes(PlaygroundStyle.paneMaximized)
					}) {
						Div({ classes(PlaygroundStyle.paneHeader) }) {
							Div({ classes(PlaygroundStyle.paneHeading) }) {
								Span({ classes(PlaygroundStyle.paneLabel) }) { Text("You write") }
								Span({ classes(PlaygroundStyle.paneTitle) }) { Text(USER_FILE_NAME) }
							}

							Div({ classes(PlaygroundStyle.paneActions) }) {
								Button({
									classes(PlaygroundStyle.iconButton)
									title(if (maximizedPane == MaximizedPane.EDITOR) "Exit fullscreen" else "Open fullscreen")
									onClick { maximizedPane = maximizedPane.toggle(MaximizedPane.EDITOR) }
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
									selectedExample = playgroundExamplesByCode[it]
								},
								onReady = { editor = it },
							)
						}

						ProblemsStrip(diagnostics) { diagnostic -> editor?.revealDiagnostic(diagnostic) }
					}

					Div({
						classes(PlaygroundStyle.splitter)
						if (resizing) classes(PlaygroundStyle.splitterActive)
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
						state = shownOutput,
						backendConfigured = backendConfigured,
						stale = ready != null && ready.code != code,
						rebuild = compiling?.takeIf { !busy }?.progress,
						maximized = maximizedPane == MaximizedPane.OUTPUT,
						onToggleMaximize = { maximizedPane = maximizedPane.toggle(MaximizedPane.OUTPUT) },
					)
				}
			}
		}
	}
}

/**
 * Loads the library chunks of the last visit's runs into the worker, from [ChunkStore], so the first rebuild of this
 * visit only evaluates the snippet. Nothing happens on a first visit or when the store lost a chunk.
 */
private suspend fun prewarmRunner() {
	val libraries = PlaygroundStorage.libraries.map { (name, hash) -> CompiledChunk(name, ChunkStore.get(hash) ?: return, hash) }
	if (libraries.isNotEmpty()) PackRunner.prewarm(libraries)
}

/** Share of the workspace width given to the editor, and the range the splitter may drag it through. */
private const val DEFAULT_SPLIT = 0.55
private const val MAX_SPLIT = 0.8
private const val MIN_SPLIT = 0.2

/** Which pane, if any, is currently taking over the viewport. */
private enum class MaximizedPane {
	EDITOR,
	NONE,
	OUTPUT;

	fun toggle(pane: MaximizedPane) = if (this == pane) NONE else pane
}

/**
 * Shows how long the visitor has been waiting, so a long compile never looks like a hung spinner.
 *
 * The compile is the one stage that cannot report a fraction of itself, so elapsed time against what past
 * compiles took stands in for it - capped short of full, since the estimate is a median and this compile
 * may well be the slow kind.
 */
private fun OutputState.withElapsed(seconds: Int, typicalMs: Int?) = when {
	this !is OutputState.Working || seconds == 0 -> this

	else -> copy(
		label = "$label... ${seconds}s",
		fraction = fraction ?: typicalMs?.let { (seconds * 1000.0 / it).coerceAtMost(ESTIMATE_CEILING) },
	)
}

/** An estimated bar never reaches the end: the compile is over when the files show up, not when a guess says so. */
private const val ESTIMATE_CEILING = 0.92
