package io.github.ayfri.kore.website.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.common.setKeywords
import io.github.ayfri.kore.website.components.layouts.PageLayout
import io.github.ayfri.kore.website.components.playground.*
import io.github.ayfri.kore.website.components.playground.language.importOnTheFly
import io.github.ayfri.kore.website.components.playground.language.registerKoreActions
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.utils.onEvents
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.varabyte.kobweb.compose.css.setVariable
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.fr
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.FileInput
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.files.get
import kotlin.js.Promise
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/** Quiet time after the last keystroke before type-checking and compiling what the editor holds. */
private val IDLE_DELAY = 700.milliseconds

/** Past this an opened file is not a snippet, and Monaco would choke on it before the backend refuses it. */
private const val MAX_OPENED_FILE_BYTES = 512 * 1024

/** Height the editors keep when the bottom panel is dragged up. */
private const val MIN_WORKSPACE_HEIGHT = 160

/** What the wait looks like before the backend has said anything, worded from this visitor's own timings. */
private fun compileHint(typicalMs: Int?) = when (typicalMs) {
	null -> "Compiling Kotlin to JavaScript against Kore. The result is cached, so running an unchanged snippet again is instant."
	else -> "Your last compiles took about ${typicalMs / 1000}s. The result is cached, so running an unchanged snippet again is instant."
}

/** What a drag on one of the two resize handles is currently moving. */
private enum class Resize {
	COLUMNS,
	NONE,
	PANEL,
}

@Page
@Composable
fun PlaygroundPage() {
	Style(HomePageStyle)
	Style(PlaygroundStyle)

	val scope = rememberCoroutineScope()
	val backendConfigured = remember { playgroundApiUrl != null }

	var baseExample by remember { mutableStateOf(defaultExample) }
	var busy by remember { mutableStateOf(false) }
	var code by remember { mutableStateOf(defaultExample.code) }
	var cursor by remember { mutableStateOf(CursorInfo(1, 1)) }
	var diagnostics by remember { mutableStateOf(emptyList<PlaygroundDiagnostic>()) }
	var editor by remember { mutableStateOf<CodeEditor?>(null) }
	var elapsedSeconds by remember { mutableStateOf(0) }
	var fileInput by remember { mutableStateOf<HTMLInputElement?>(null) }
	var initialCode by remember { mutableStateOf<String?>(null) }
	var mainColumn by remember { mutableStateOf<HTMLElement?>(null) }
	var output by remember { mutableStateOf<OutputState>(OutputState.Idle) }
	var resizing by remember { mutableStateOf(Resize.NONE) }
	var runnerPrewarmed by remember { mutableStateOf(false) }
	var shareLabel by remember { mutableStateOf("Share") }
	var shortcutsOpen by remember { mutableStateOf(false) }
	var toast by remember { mutableStateOf<String?>(null) }
	var typeChecked by remember { mutableStateOf(false) }
	var typicalCompileMs by remember { mutableStateOf<Int?>(null) }
	var workspace by remember { mutableStateOf<HTMLElement?>(null) }

	fun notify(message: String) {
		toast = message
		BuildLog.add(LogLevel.INFO, message)
	}

	fun showDiagnostics(list: List<PlaygroundDiagnostic>) {
		diagnostics = list
		editor?.showDiagnostics(list)
	}

	/** Shows the pack an untouched example generates, computed at build time. False when [target] is not an example. */
	suspend fun showPrecomputed(target: String): Boolean {
		val example = playgroundExamplesByCode[target] ?: return false
		val files = example.precomputedFiles() ?: return false

		if (code == target && (output as? OutputState.Ready)?.code != target) {
			output = OutputState.Ready(files, target, OutputOrigin.Precomputed)
			BuildLog.add(LogLevel.SUCCESS, "${example.title}: showing the pack generated at build time", "${files.size} files")
		}

		return true
	}

	/**
	 * Compiles [target], runs it in a worker and shows the pack, unless the buffer moved on meanwhile.
	 *
	 * A [live] build is one the page started on idle: it keeps the current output on screen instead of a waiting
	 * state, leaves compile errors to the squiggles and the problems panel, and drops a full compile queue
	 * silently, since nobody pressed anything. Only a pack that fails while running replaces the output.
	 */
	suspend fun build(target: String, live: Boolean) {
		/** A live build of a buffer already compiled is the same use again, so only its first build counts. */
		fun report(outcome: String, cached: Boolean = false, vararg extra: Pair<String, Any>) {
			if (live && cached) return
			PlaygroundAnalytics.track(
				"playground_run",
				"base_example" to baseExample.slug,
				"outcome" to outcome,
				"trigger" to if (live) "auto" else "run",
				*extra,
			)
		}

		val result = try {
			PlaygroundCompiler.compile(target)
		} catch (cancelled: CancellationException) {
			throw cancelled
		} catch (busyBackend: CompileBusyException) {
			BuildLog.add(LogLevel.WARNING, "The compile backend is busy", busyBackend.message)
			report("busy")
			if (!live) output = OutputState.Failed("The compile backend is busy", busyBackend.message)
			return
		} catch (failure: Throwable) {
			BuildLog.add(LogLevel.ERROR, "Could not reach the compile backend", failure.message)
			report("unreachable")
			if (!live) output = OutputState.Failed("Could not reach the compile backend", failure.message)
			return
		}

		if (code != target) return
		showDiagnostics(result.diagnostics)
		typeChecked = true

		if (!result.succeeded) {
			BuildLog.add(LogLevel.ERROR, "Compilation failed", result.exception ?: "${result.errors.size} errors")
			report("compile_error", result.cached)
			if (live) return

			if (result.errors.isNotEmpty()) PlaygroundLayout.showPanel(PanelTab.PROBLEMS)
			output = OutputState.Failed(
				title = "Compilation failed",
				detail = result.exception
					?: result.errors.joinToString("\n") { "${it.startLine}:${it.startColumn}  ${it.message}" }
						.ifEmpty { "The compiler returned no code and no message." },
			)
			return
		}

		if (result.cached) BuildLog.add(LogLevel.SUCCESS, "Reused a cached compile")
		else {
			BuildLog.add(LogLevel.SUCCESS, "Compiled in ${result.durationMs / 100 / 10.0}s", "${result.chunks.size} modules")
			PlaygroundStorage.recordCompile(result.durationMs)
			typicalCompileMs = PlaygroundStorage.typicalCompileMs
		}

		if (!live) output = OutputState.Working("Running", "The compiled pack is built in your browser, inside a worker.")

		val execution = PackRunner.run(result.evaluationOrder) { progress ->
			if (!live) output = OutputState.Working(progress.label, progress.detail, progress.fraction)
		}

		if (code != target) return

		output = when (execution) {
			is RunResult.Success -> {
				PlaygroundStorage.libraries = result.evaluationOrder.dropLast(1).mapNotNull { chunk -> chunk.hash?.let { chunk.name to it } }
				BuildLog.add(
					LogLevel.SUCCESS,
					"Generated ${execution.files.size} files in ${execution.durationMs}ms",
					humanSize(execution.files.sumOf { it.content.length }),
				)
				report("success", result.cached, "compile_ms" to result.durationMs, "files" to execution.files.size)

				OutputState.Ready(
					files = execution.files,
					code = target,
					origin = OutputOrigin.Compiled(result.durationMs, execution.durationMs, result.cached),
				)
			}

			is RunResult.Failure -> {
				BuildLog.add(LogLevel.ERROR, "The snippet failed while running", execution.message)
				report("runtime_error", result.cached)
				OutputState.Failed("The snippet failed while running", execution.message)
			}
		}
	}

	fun run() {
		if (busy) return
		val target = code

		scope.launch {
			busy = true

			try {
				when {
					showPrecomputed(target) -> PlaygroundAnalytics.track(
						"playground_run",
						"base_example" to baseExample.slug,
						"outcome" to "precomputed",
						"trigger" to "run",
					)

					!backendConfigured -> {
						BuildLog.add(LogLevel.ERROR, "Compiling is unavailable here", "This deployment has no compile backend.")
						output = OutputState.Failed(
							"Compiling is unavailable here",
							"This deployment has no compile backend, so only the examples run as they are.",
						)
					}

					else -> {
						BuildLog.add(LogLevel.INFO, "Run requested")
						output = OutputState.Working("Compiling", compileHint(typicalCompileMs))
						build(target, live = false)
					}
				}
			} finally {
				busy = false
			}
		}
	}

	fun selectExample(example: PlaygroundExample) {
		baseExample = example
		editor?.replaceContent(example.code) ?: run { code = example.code }
		PlaygroundAnalytics.track("playground_example", "example" to example.slug)
		// Under `lgMax` the sidebar covers the editor, so picking an example is also leaving it.
		if (window.matchMedia("(max-width: 1023px)").matches) PlaygroundLayout.sidebarOpen = false
	}

	fun openFile(input: HTMLInputElement) {
		val file = input.files?.get(0) ?: return
		input.value = ""

		if (file.size.toInt() > MAX_OPENED_FILE_BYTES) {
			BuildLog.add(LogLevel.WARNING, "${file.name} is too large to open", humanSize(file.size.toInt()))
			return
		}

		scope.launch {
			val text = file.asDynamic().text().unsafeCast<Promise<String>>().await()
			editor?.replaceContent(text) ?: run { code = text }
			notify("Opened ${file.name}")
			PlaygroundAnalytics.track("playground_open_file")
		}
	}

	fun execute(command: PlaygroundCommand) {
		val ready = output as? OutputState.Ready

		when (command) {
			PlaygroundCommand.DOWNLOAD_SOURCE -> {
				downloadText(code, USER_FILE_NAME)
				notify("Downloaded $USER_FILE_NAME")
			}

			PlaygroundCommand.DOWNLOAD_ZIP -> ready?.let {
				downloadZip(it.files)
				notify("Downloaded ${zipName(it.files)}")
			}

			PlaygroundCommand.FOCUS_MODE -> PlaygroundLayout.focusMode = !PlaygroundLayout.focusMode
			PlaygroundCommand.OPEN_FILE -> fileInput?.click()
			PlaygroundCommand.RESET -> editor?.replaceContent(baseExample.code) ?: run { code = baseExample.code }
			PlaygroundCommand.RUN -> run()

			PlaygroundCommand.SAVE -> {
				PlaygroundStorage.draft = code
				notify("Draft saved in this browser")
			}

			PlaygroundCommand.SHARE -> scope.launch {
				val url = shareUrl(code)
				window.history.replaceState(null, "", url)
				runCatching { window.navigator.asDynamic().clipboard.writeText(url) }
				notify("Share link copied")
				PlaygroundAnalytics.track("playground_share", "base_example" to baseExample.slug)
				shareLabel = "Copied"
				delay(2.seconds)
				shareLabel = "Share"
			}

			PlaygroundCommand.SHORTCUTS -> shortcutsOpen = !shortcutsOpen
			PlaygroundCommand.TOGGLE_AUTO_BUILD -> PlaygroundSettings.autoBuild = !PlaygroundSettings.autoBuild
			PlaygroundCommand.TOGGLE_PANEL -> PlaygroundLayout.panelOpen = !PlaygroundLayout.panelOpen
			PlaygroundCommand.TOGGLE_SIDEBAR -> PlaygroundLayout.sidebarOpen = !PlaygroundLayout.sidebarOpen
		}
	}

	// A shared link wins over a linked example, then a restored draft, then the default example. Resolved before the
	// editor is created, since Monaco only reads its initial value once.
	LaunchedEffect(Unit) {
		val shared = sharedCode()
		val linked = linkedExample()
		val draft = PlaygroundStorage.draft
		code = shared ?: linked?.code ?: draft ?: defaultExample.code
		// Dropped once read, so reloading the page restores the visitor's edits instead of the example again.
		if (linked != null) window.history.replaceState(null, "", window.location.pathname)
		baseExample = playgroundExamplesByCode[code]
			?: playgroundExamples.firstOrNull { it.slug == PlaygroundStorage.exampleSlug }
			?: defaultExample
		initialCode = code
		typicalCompileMs = PlaygroundStorage.typicalCompileMs
		// The examples start open only where they leave the editors enough room, a narrow screen always starts on the code.
		val firstVisit = PlaygroundStorage.read("sidebarOpen") == null
		if (window.matchMedia(if (firstVisit) "(max-width: 1439px)" else "(max-width: 1023px)").matches) PlaygroundLayout.sidebarOpen = false

		BuildLog.add(
			LogLevel.INFO,
			when {
				shared != null -> "Opened a shared snippet"
				linked == null && draft != null -> "Restored your last draft"
				else -> "Loaded the ${baseExample.title} example"
			},
		)
		PlaygroundAnalytics.track(
			"playground_open",
			"base_example" to baseExample.slug,
			"entry" to when {
				shared != null -> "shared"
				linked != null -> "example_link"
				draft != null -> "draft"
				else -> "default"
			},
		)
		showPrecomputed(code)
	}

	// Debounced through the effect itself: a new keystroke cancels the pending write.
	LaunchedEffect(code) {
		if (initialCode == null) return@LaunchedEffect
		delay(400.milliseconds)
		PlaygroundStorage.draft = code
	}

	LaunchedEffect(baseExample) { PlaygroundStorage.exampleSlug = baseExample.slug }

	// Typing back to an untouched example shows its pack again, and to a buffer compiled before, its diagnostics. Otherwise,
	// once typing pauses, the JVM type-check and the real compile start together: the type-check draws squiggles in well
	// under a second, and a clean buffer is linked and run without waiting for Run, so the pack is usually there before
	// the click. The backend compiles one snippet at a time for everyone, so a buffer that already failed its type-check
	// is not compiled live, and a compile still waiting behind another one is dropped when the type-check finds errors.
	// A new keystroke cancels all of it, and a compile already sent still lands in the cache. With live rebuild off, only
	// the type-check runs.
	LaunchedEffect(code) {
		if (initialCode == null) return@LaunchedEffect

		if (code in playgroundExamplesByCode) {
			showDiagnostics(emptyList())
			typeChecked = true
			showPrecomputed(code)
			return@LaunchedEffect
		}

		if (!backendConfigured) return@LaunchedEffect
		val autoBuild = PlaygroundSettings.autoBuild

		if (autoBuild && !runnerPrewarmed) {
			runnerPrewarmed = true
			scope.launch { prewarmRunner() }
		}

		delay(IDLE_DELAY)

		val target = code
		CompileMemo.get(target)?.let { compiled ->
			typeChecked = true
			if (!busy) showDiagnostics(compiled.diagnostics)
			if (autoBuild) build(target, live = true)
			return@LaunchedEffect
		}

		val liveBuild = if (autoBuild && !PlaygroundCompiler.failedTypeCheck(target)) launch { build(target, live = true) } else null
		launch {
			val startedAt = window.performance.now()
			val fresh = runCatching { PlaygroundCompiler.typeCheck(target) }
				.onFailure { BuildLog.add(LogLevel.WARNING, "Type-check failed", it.message) }
				.getOrNull() ?: return@launch

			val problems = fresh.filter { it.file == USER_FILE_NAME }
			val waiting = PlaygroundCompiler.running.let { it != null && it.code != target }
			if (waiting && problems.any { it.severity == DiagnosticSeverity.ERROR }) liveBuild?.cancel()

			BuildLog.add(
				LogLevel.INFO,
				"Type-checked $USER_FILE_NAME in ${(window.performance.now() - startedAt).toInt()}ms",
				problems.groupingBy { it.severity }.eachCount().entries.joinToString { (severity, count) -> "$count ${severity.name.lowercase()}" }
					.ifEmpty { "no problems" },
			)

			typeChecked = true
			// A Run in flight owns the markers: its JS diagnostics are the ones that matter.
			if (!busy) showDiagnostics(fresh)

			if (PlaygroundSettings.autoImport) editor?.importOnTheFly(problems, target)?.takeIf { it.isNotEmpty() }?.let { imported ->
				BuildLog.add(LogLevel.INFO, "Imported ${imported.joinToString { it.substringAfterLast('.') }}", imported.joinToString("\n"))
			}
		}
	}

	LaunchedEffect(busy) {
		elapsedSeconds = 0
		while (busy) {
			delay(1.seconds)
			elapsedSeconds++
		}
	}

	LaunchedEffect(toast) {
		if (toast == null) return@LaunchedEffect
		delay(2200.milliseconds)
		toast = null
	}

	// Each new step the backend reports for any compile of this tab, joined or started here, lands in the build log.
	val job = PlaygroundCompiler.running
	LaunchedEffect(job, job?.progress?.label) {
		job?.progress?.let { BuildLog.add(LogLevel.INFO, it.label, it.detail) }
	}

	document.onEvents(
		"mousemove" to { event ->
			val mouse = event as MouseEvent

			when (resizing) {
				Resize.COLUMNS -> workspace?.getBoundingClientRect()?.let { rect ->
					PlaygroundLayout.splitFraction = ((mouse.clientX - rect.left) / rect.width).coerceIn(PlaygroundLayout.SPLIT_RANGE)
				}

				Resize.PANEL -> mainColumn?.getBoundingClientRect()?.let { rect ->
					val max = maxOf(PlaygroundLayout.PANEL_HEIGHTS.first, (rect.height - MIN_WORKSPACE_HEIGHT).toInt())
					PlaygroundLayout.panelHeight = (rect.bottom - mouse.clientY).toInt().coerceIn(PlaygroundLayout.PANEL_HEIGHTS.first, max)
				}

				Resize.NONE -> Unit
			}
		},
		"mouseup" to { resizing = Resize.NONE },
		key = resizing,
	)

	val currentExecute by rememberUpdatedState(::execute)

	DisposableEffect(Unit) {
		val listener = { event: Event ->
			val keyboard = event.unsafeCast<KeyboardEvent>()

			// Monaco stops the keys it handles itself, a command bound in the editor included, so these never run twice.
			PlaygroundCommand.of(keyboard)?.takeUnless { keyboard.defaultPrevented }?.let {
				keyboard.preventDefault()
				currentExecute(it)
			}

			// One layer per press: the dialog, then a maximized pane, then focus mode.
			if (keyboard.key == "Escape" && !keyboard.defaultPrevented) when {
				shortcutsOpen -> shortcutsOpen = false
				PlaygroundLayout.maximizedPane != MaximizedPane.NONE -> PlaygroundLayout.maximizedPane = MaximizedPane.NONE
				else -> PlaygroundLayout.focusMode = false
			}
		}

		document.addEventListener("keydown", listener)
		onDispose { document.removeEventListener("keydown", listener) }
	}

	// A compile of this very buffer, started by the idle path or by Run, is what the output pane reports.
	val compiling = job?.takeIf { it.code == code }
	val ready = output as? OutputState.Ready
	val problems = diagnostics.filter { it.file == USER_FILE_NAME }
	val dirty = code != baseExample.code
	val shownOutput = when {
		busy && output is OutputState.Working && compiling != null -> compiling.progress.let {
			OutputState.Working(it.label, it.detail, it.fraction)
		}

		else -> output
	}.withElapsed(elapsedSeconds, typicalCompileMs)

	PageLayout("Playground - Try Kore in your browser") {
		setDescription("Write Kotlin, get a Minecraft datapack. Try the Kore DSL in a browser IDE with instant examples, live rebuilds and a zip download.")
		setKeywords(
			"kore playground", "kotlin datapack editor", "minecraft datapack generator online",
			"try kore", "datapack builder", "kotlin dsl playground"
		)

		Div({ classes(HomePageStyle.page, PlaygroundStyle.page) }) {
			Div({
				classes(PlaygroundStyle.ide)
				if (PlaygroundLayout.focusMode) classes(PlaygroundStyle.ideFocus)
				when (resizing) {
					Resize.COLUMNS -> classes(PlaygroundStyle.resizingColumns)
					Resize.PANEL -> classes(PlaygroundStyle.resizingRows)
					Resize.NONE -> Unit
				}
			}) {
				TitleBar(
					example = baseExample,
					dirty = dirty,
					busy = busy,
					canRun = backendConfigured || code in playgroundExamplesByCode,
					hasPack = ready != null,
					shareLabel = shareLabel,
					onCommand = ::execute,
					onOpenExamples = { PlaygroundLayout.toggleSidebar(SidebarView.EXAMPLES) },
				)

				Div({ classes(PlaygroundStyle.ideBody) }) {
					ActivityBar(problems.size, ::execute)

					if (PlaygroundLayout.sidebarOpen) {
						SidePanel(baseExample, dirty, ::selectExample)
						Div({
							classes(PlaygroundStyle.sideScrim)
							onClick { PlaygroundLayout.sidebarOpen = false }
						})
					}

					Div({
						classes(PlaygroundStyle.mainColumn)
						ref {
							mainColumn = it
							onDispose { mainColumn = null }
						}
					}) {
						Div({
							classes(PlaygroundStyle.workspace)
							ref {
								workspace = it
								onDispose { workspace = null }
							}

							style {
								// A ratio rather than a column list, so the narrow-screen media query can still collapse to one column.
								val split = PlaygroundLayout.splitFraction
								setVariable(PlaygroundVars.Split, (split / (1 - split)).fr)
							}
						}) {
							EditorPane(
								initialCode = initialCode,
								editor = editor,
								dirty = dirty,
								errorCount = problems.count { it.severity == DiagnosticSeverity.ERROR },
								onChange = {
									code = it
									playgroundExamplesByCode[it]?.let { example -> baseExample = example }
								},
								onCursor = { cursor = it },
								onReady = { monaco, created ->
									editor = created
									created.registerCommands(monaco) { currentExecute(it) }
									created.registerKoreActions(monaco) { imported ->
										notify(if (imported.isEmpty()) "No missing import has a single match" else "Imported ${imported.joinToString { it.substringAfterLast('.') }}")
									}
								},
								onReset = { execute(PlaygroundCommand.RESET) },
							)

							Div({
								classes(PlaygroundStyle.splitter)
								if (resizing == Resize.COLUMNS) classes(PlaygroundStyle.splitterActive)
								attr("aria-orientation", "vertical")
								attr("role", "separator")
								title("Drag to resize, double-click to reset")
								onDoubleClick { PlaygroundLayout.splitFraction = PlaygroundLayout.DEFAULT_SPLIT }
								onMouseDown {
									it.preventDefault()
									resizing = Resize.COLUMNS
								}
							})

							OutputPanel(
								state = shownOutput,
								backendConfigured = backendConfigured,
								stale = ready != null && ready.code != code,
								rebuild = compiling?.takeIf { !busy }?.progress,
								onCopy = { text, what ->
									runCatching { window.navigator.asDynamic().clipboard.writeText(text) }
									notify("$what copied")
								},
								onShowProblems = { PlaygroundLayout.showPanel(if (problems.isEmpty()) PanelTab.LOG else PanelTab.PROBLEMS) },
							)
						}

						if (PlaygroundLayout.panelOpen) {
							Div({
								classes(PlaygroundStyle.panelResizer)
								if (resizing == Resize.PANEL) classes(PlaygroundStyle.splitterActive)
								attr("aria-orientation", "horizontal")
								attr("role", "separator")
								title("Drag to resize")
								onMouseDown {
									it.preventDefault()
									resizing = Resize.PANEL
								}
							})

							Div({
								classes(PlaygroundStyle.panelSlot)
								style { height(PlaygroundLayout.panelHeight.px) }
							}) {
								BottomPanel(diagnostics, typeChecked, backendConfigured) { diagnostic -> editor?.revealDiagnostic(diagnostic) }
							}
						}
					}
				}

				StatusBar(
					backendConfigured = backendConfigured,
					activity = when {
						job != null -> job.progress.label + if (busy && elapsedSeconds > 0) " ${elapsedSeconds}s" else ""
						busy -> "Running"
						else -> null
					},
					errors = problems.count { it.severity == DiagnosticSeverity.ERROR },
					warnings = problems.count { it.severity == DiagnosticSeverity.WARNING },
					cursor = cursor,
					output = output,
					onGoToLine = { editor?.runAction("editor.action.gotoLine") },
				)

				toast?.let { Toast(it) }
				if (shortcutsOpen) ShortcutsDialog { shortcutsOpen = false }

				FileInput {
					classes(PlaygroundStyle.hiddenInput)
					attr("accept", ".kt,.kts,.txt")
					attr("aria-hidden", "true")
					attr("tabindex", "-1")
					ref {
						fileInput = it
						onDispose { fileInput = null }
					}
					onChange { event -> openFile(event.target) }
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
