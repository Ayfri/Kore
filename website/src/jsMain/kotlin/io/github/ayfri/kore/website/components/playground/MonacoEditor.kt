package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.externals.monaco.Disposable
import io.github.ayfri.kore.website.externals.monaco.EditOperation
import io.github.ayfri.kore.website.externals.monaco.EditorOptions
import io.github.ayfri.kore.website.externals.monaco.MarkerData
import io.github.ayfri.kore.website.externals.monaco.Monaco
import io.github.ayfri.kore.website.externals.monaco.Position
import io.github.ayfri.kore.website.externals.monaco.loadMonaco
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

/** `fixedOverflowWidgets` lets hovers and suggestions escape the pane, whose `overflow: hidden` would clip them. */
private fun editorOptions(value: String): EditorOptions = PlaygroundSettings.applyTo((js("({})").unsafeCast<EditorOptions>()).apply {
	automaticLayout = true
	bracketPairColorization = js("({ enabled: true })")
	cursorBlinking = "smooth"
	cursorSmoothCaretAnimation = "on"
	fixedOverflowWidgets = true
	fontFamily = "'JetBrains Mono', 'Fira Code', 'Cascadia Code', Consolas, monospace"
	guides = js("({ bracketPairs: true, highlightActiveBracketPair: true, highlightActiveIndentation: true, indentation: true })")
	insertSpaces = false
	language = "kotlin"
	padding = js("({ top: 12, bottom: 12 })")
	scrollBeyondLastLine = false
	smoothScrolling = true
	tabSize = 4
	theme = MONACO_THEME_NAME
	this.value = value
})

/** Where the caret is and how many characters are selected, for the status bar. */
data class CursorInfo(val line: Int, val column: Int, val selected: Int = 0)

/** Monaco marker severities, mirroring `monaco.MarkerSeverity`, which the ESM bundle only exposes at runtime. */
private const val MARKER_SEVERITY_ERROR = 8
private const val MARKER_SEVERITY_WARNING = 4
private const val MARKER_SEVERITY_INFO = 2

private fun markerOf(diagnostic: PlaygroundDiagnostic): MarkerData = (js("({})").unsafeCast<MarkerData>()).apply {
	severity = when (diagnostic.severity) {
		DiagnosticSeverity.ERROR -> MARKER_SEVERITY_ERROR
		DiagnosticSeverity.WARNING -> MARKER_SEVERITY_WARNING
		DiagnosticSeverity.INFO -> MARKER_SEVERITY_INFO
	}

	message = diagnostic.message
	startLineNumber = diagnostic.startLine
	startColumn = diagnostic.startColumn
	endLineNumber = diagnostic.endLine
	endColumn = maxOf(diagnostic.endColumn, diagnostic.startColumn + 1)
}

/** Draws compiler diagnostics as squiggles. Harness messages are skipped, they belong to no visible file. */
fun CodeEditor.showDiagnostics(diagnostics: List<PlaygroundDiagnostic>) {
	val model = getModel() ?: return
	val markers = diagnostics.filter { it.file == USER_FILE_NAME }.map(::markerOf).toTypedArray()

	loadMonaco().then { monaco -> monaco.editor.setModelMarkers(model, "kore", markers) }
}

/** Moves the caret to [line]:[column], scrolls it into view and focuses the editor. */
fun CodeEditor.reveal(line: Int, column: Int) {
	setPosition((js("({})").unsafeCast<Position>()).apply {
		asDynamic().lineNumber = line
		asDynamic().column = column
	})

	revealLineInCenter(line)
	focus()
}

fun CodeEditor.revealDiagnostic(diagnostic: PlaygroundDiagnostic) = reveal(diagnostic.startLine, diagnostic.startColumn)

/** Replaces the whole buffer as one undoable edit, so switching examples or opening a file never loses the code. */
fun CodeEditor.replaceContent(text: String) {
	val model = getModel() ?: return setValue(text)

	pushUndoStop()
	executeEdits("kore", arrayOf((js("({})").unsafeCast<EditOperation>()).apply {
		range = model.getFullModelRange()
		this.text = text
		forceMoveMarkers = true
	}))
	pushUndoStop()
	reveal(1, 1)
}

/** Runs one of Monaco's own actions (`actions.find`, `editor.foldAll`...) as if its shortcut had been pressed. */
fun CodeEditor.runAction(id: String) {
	focus()
	trigger("kore", id, null)
}

/**
 * Monaco is loaded and created after mount, never during composition, and disposed on teardown.
 *
 * It is skipped entirely while `kobweb export` snapshots the site: the export runs the page in a headless
 * browser where Monaco's web workers fail, which crashes the route. The exported HTML only needs the empty
 * container - real visitors hydrate the editor client-side.
 *
 * The Monaco bundle is a few megabytes, so a placeholder holds the pane until `create` returns - an empty
 * box for that long reads as a broken editor.
 *
 * [onReady] hands Monaco and the editor back so callers can read the buffer, set markers, or register actions, and
 * [PlaygroundSettings] changes reach the live editor through `updateOptions`.
 */
@Composable
fun MonacoEditor(
	initialValue: String,
	className: String,
	onChange: (String) -> Unit = {},
	onCursor: (CursorInfo) -> Unit = {},
	onReady: (Monaco, CodeEditor) -> Unit = { _, _ -> },
) {
	var container by remember { mutableStateOf<HTMLElement?>(null) }
	var instance by remember { mutableStateOf<CodeEditor?>(null) }
	val currentOnChange by rememberUpdatedState(onChange)
	val currentOnCursor by rememberUpdatedState(onCursor)
	val currentOnReady by rememberUpdatedState(onReady)
	val options = PlaygroundSettings.applyTo(js("({})").unsafeCast<EditorOptions>())
	LaunchedEffect(instance, JSON.stringify(options)) { instance?.updateOptions(options) }

	Div({ classes(className, PlaygroundStyle.editorHost) }) {
		Div({
			classes(PlaygroundStyle.editorSurface)
			ref {
				container = it
				onDispose { container = null }
			}
		})

		if (instance == null) Div({ classes(PlaygroundStyle.editorLoading) }) {
			Div({ classes(PlaygroundStyle.spinner) })
			Span({ classes(PlaygroundStyle.stateDetail) }) { Text("Loading the editor...") }
		}
	}

	DisposableEffect(container) {
		val element = container ?: return@DisposableEffect onDispose { }
		if (AppGlobals.isExporting) return@DisposableEffect onDispose { }

		var created: CodeEditor? = null
		val subscriptions = mutableListOf<Disposable>()
		var disposed = false

		loadMonaco().then { monaco ->
			// The route can be left while the chunk is still downloading; don't build an orphan editor.
			if (disposed) return@then

			defineKoreTheme(monaco.editor)

			val editor = monaco.editor.create(element, editorOptions(initialValue))
			created = editor
			subscriptions += editor.onDidChangeModelContent { currentOnChange(editor.getValue()) }
			subscriptions += editor.onDidChangeCursorSelection { event ->
				val selection = event.selection
				val selected = (listOf(selection) + event.secondarySelections).sumOf { editor.getModel()?.getValueLengthInRange(it) ?: 0 }
				currentOnCursor(CursorInfo(selection.positionLineNumber, selection.positionColumn, selected))
			}

			instance = editor
			currentOnReady(monaco, editor)
		}

		onDispose {
			disposed = true
			instance = null
			subscriptions.forEach { it.dispose() }
			created?.dispose()
		}
	}
}
