package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.externals.monaco.EditorOptions
import io.github.ayfri.kore.website.externals.monaco.MarkerData
import io.github.ayfri.kore.website.externals.monaco.Position
import io.github.ayfri.kore.website.externals.monaco.loadMonaco
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

private fun editorOptions(value: String): EditorOptions = (js("({})").unsafeCast<EditorOptions>()).apply {
	automaticLayout = true
	bracketPairColorization = js("({ enabled: true })")
	fontFamily = "'JetBrains Mono', 'Fira Code', 'Cascadia Code', Consolas, monospace"
	fontLigatures = true
	fontSize = 14
	guides = js("({ bracketPairs: true, highlightActiveBracketPair: true, highlightActiveIndentation: true, indentation: true })")
	insertSpaces = false
	language = "kotlin"
	minimap = js("({ enabled: false })")
	padding = js("({ top: 16, bottom: 16 })")
	renderWhitespace = "selection"
	scrollBeyondLastLine = false
	tabSize = 4
	theme = MONACO_THEME_NAME
	this.value = value
	wordWrap = "off"
}

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

/** Moves the caret to a diagnostic and scrolls it into view. */
fun CodeEditor.revealDiagnostic(diagnostic: PlaygroundDiagnostic) {
	setPosition((js("({})").unsafeCast<Position>()).apply {
		asDynamic().lineNumber = diagnostic.startLine
		asDynamic().column = diagnostic.startColumn
	})

	revealLineInCenter(diagnostic.startLine)
	focus()
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
 * [onReady] hands the editor instance back so callers can read the buffer, set markers, or attach providers.
 */
@Composable
fun MonacoEditor(
	initialValue: String,
	className: String,
	onChange: (String) -> Unit = {},
	onReady: (CodeEditor) -> Unit = {},
) {
	var container by remember { mutableStateOf<HTMLElement?>(null) }
	var mounted by remember { mutableStateOf(false) }
	val currentOnChange by rememberUpdatedState(onChange)
	val currentOnReady by rememberUpdatedState(onReady)

	Div({ classes(className, PlaygroundStyle.editorHost) }) {
		Div({
			classes(PlaygroundStyle.editorSurface)
			ref {
				container = it
				onDispose { container = null }
			}
		})

		if (!mounted) Div({ classes(PlaygroundStyle.editorLoading) }) {
			Div({ classes(PlaygroundStyle.spinner) })
			Span({ classes(PlaygroundStyle.stateDetail) }) { Text("Loading the editor...") }
		}
	}

	DisposableEffect(container) {
		val element = container ?: return@DisposableEffect onDispose { }
		if (AppGlobals.isExporting) return@DisposableEffect onDispose { }

		var instance: CodeEditor? = null
		var changeSubscription: io.github.ayfri.kore.website.externals.monaco.Disposable? = null
		var disposed = false

		loadMonaco().then { monaco ->
			// The route can be left while the chunk is still downloading; don't build an orphan editor.
			if (disposed) return@then

			defineMaterialDarkerTheme(monaco.editor)

			val created = monaco.editor.create(element, editorOptions(initialValue))
			instance = created
			changeSubscription = created.onDidChangeModelContent { currentOnChange(created.getValue()) }
			mounted = true
			currentOnReady(created)
		}

		onDispose {
			disposed = true
			mounted = false
			changeSubscription?.dispose()
			instance?.dispose()
		}
	}
}
