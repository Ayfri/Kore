package io.github.ayfri.kore.website.externals.monaco

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLScriptElement
import kotlin.js.Json
import kotlin.js.Promise

/**
 * Monaco is deliberately **not** declared with `@file:JsModule`, and is not bundled by webpack at all.
 *
 * Two constraints rule that out. A static module reference puts all ~6 MB of Monaco in the main bundle and
 * evaluates it on every page, injecting its stylesheet into the docs and landing pages too. A dynamic
 * `import()` does code-split, but `kobwebExport` copies exactly one script file into the exported site, so
 * every generated chunk would 404 in production.
 *
 * Instead Monaco's ESM distribution is pre-bundled by esbuild into static assets under `/monaco` (see the
 * `bundleMonaco` Gradle task) and pulled in at runtime by [loadMonaco]. Everything declared here is
 * type-only - `external interface` erases at runtime - so none of it emits an import.
 */
external interface Monaco {
	val editor: MonacoEditor

	/** `monaco.KeyCode`, a numeric enum whose names match `KeyboardEvent.code` (`Enter`, `KeyS`...). */
	@Suppress("PropertyName")
	val KeyCode: Any

	@Suppress("PropertyName")
	val KeyMod: KeyMods
}

@Suppress("PropertyName")
external interface KeyMods {
	val CtrlCmd: Int
	val Shift: Int
}

external interface MonacoEditor {
	fun create(domElement: HTMLElement, options: EditorOptions): CodeEditor
	fun defineTheme(themeName: String, themeData: ThemeData)
	fun setModelMarkers(model: TextModel, owner: String, markers: Array<MarkerData>)
}

external interface Disposable {
	fun dispose()
}

external interface TextModel {
	fun getFullModelRange(): Range
	fun getValueLengthInRange(range: Range): Int
}

external interface Position {
	var column: Int
	var lineNumber: Int
}

external interface Range {
	val endColumn: Int
	val endLineNumber: Int
	val startColumn: Int
	val startLineNumber: Int
}

/** A [Range] that also knows which end the caret sits on. */
external interface Selection : Range {
	val positionColumn: Int
	val positionLineNumber: Int
}

external interface CursorSelectionChangedEvent {
	val secondarySelections: Array<Selection>
	val selection: Selection
}

/** An entry of the editor's command palette (F1), optionally bound to keys and listed in the context menu. */
external interface ActionDescriptor {
	var contextMenuGroupId: String?
	var contextMenuOrder: Double?
	var id: String
	var keybindings: Array<Int>?
	var label: String
	var run: (CodeEditor) -> Unit
}

external interface EditOperation {
	var forceMoveMarkers: Boolean?
	var range: Range
	var text: String
}

external interface CodeEditor : Disposable {
	fun addAction(descriptor: ActionDescriptor): Disposable
	fun executeEdits(source: String, edits: Array<EditOperation>): Boolean
	fun focus()
	fun getModel(): TextModel?
	fun getValue(): String
	fun onDidChangeCursorSelection(listener: (CursorSelectionChangedEvent) -> Unit): Disposable
	fun onDidChangeModelContent(listener: () -> Unit): Disposable
	fun pushUndoStop(): Boolean
	fun revealLineInCenter(lineNumber: Int)
	fun setPosition(position: Position)
	fun setValue(newValue: String)
	fun trigger(source: String, handlerId: String, payload: Any?)
	fun updateOptions(newOptions: EditorOptions)
}

/**
 * Only the options the playground actually sets. Monaco accepts far more; add fields here as needed rather
 * than reaching for `dynamic`.
 */
external interface EditorOptions {
	var automaticLayout: Boolean?
	var cursorBlinking: String?
	var cursorSmoothCaretAnimation: String?
	var fixedOverflowWidgets: Boolean?
	var fontFamily: String?
	var fontLigatures: Boolean?
	var fontSize: Int?
	var guides: GuidesOptions?
	var insertSpaces: Boolean?
	var language: String?
	var lineNumbers: String?
	var minimap: MinimapOptions?
	var padding: PaddingOptions?
	var renderWhitespace: String?
	var scrollBeyondLastLine: Boolean?
	var smoothScrolling: Boolean?
	var stickyScroll: StickyScrollOptions?
	var tabSize: Int?
	var theme: String?
	var value: String?
	var wordWrap: String?
	var wordWrapIndicator: Boolean?
}

external interface StickyScrollOptions {
	var enabled: Boolean?
}

external interface GuidesOptions {
	var bracketPairs: Boolean?
}

external interface MinimapOptions {
	var enabled: Boolean?
}

external interface PaddingOptions {
	var top: Int?
	var bottom: Int?
}

external interface ThemeData {
	var base: String
	var inherit: Boolean
	var rules: Array<TokenThemeRule>
	var colors: Json
}

external interface TokenThemeRule {
	var token: String
	var foreground: String?
	var background: String?
	var fontStyle: String?
}

external interface MarkerData {
	var severity: Int
	var message: String
	var startLineNumber: Int
	var startColumn: Int
	var endLineNumber: Int
	var endColumn: Int
}

private const val MONACO_BASE = "/monaco"

private var loading: Promise<Monaco>? = null

/**
 * Loads Monaco from the pre-bundled ESM assets on demand, memoised so a second visit to the playground is
 * instant and a re-render mid-download never starts a second load.
 *
 * The bundle is reached through an injected `<script type="module">` rather than a Kotlin-side `import()`:
 * webpack rewrites every `import()` it can see, and this URL must stay a runtime one so the language chunks
 * next to it resolve against `/monaco` in the exported site.
 */
fun loadMonaco(): Promise<Monaco> = loading ?: Promise { resolve, reject ->
	val already = js("globalThis.monaco")
	if (already != null && already != undefined) {
		resolve(already.unsafeCast<Monaco>())
		return@Promise
	}

	// esbuild emits Monaco's styles as a sibling file; the editor renders unusable without them.
	val styles = document.createElement("link") as HTMLLinkElement
	styles.rel = "stylesheet"
	styles.href = "$MONACO_BASE/monaco.css"
	document.head!!.appendChild(styles)

	// The workers are bundled as classic (IIFE) scripts, so they start straight from their URL: same origin,
	// no module worker, no blob proxy. Only Kotlin and JSON are bundled, so `json` is the only extra label.
	js(
		"globalThis.MonacoEnvironment = { getWorker: function (workerId, label) {" +
			"return new Worker(label === 'json' ? '$MONACO_BASE/json.worker.js' : '$MONACO_BASE/editor.worker.js') } }"
	)

	window.addEventListener("monaco-ready", { resolve(js("globalThis.monaco").unsafeCast<Monaco>()) })
	window.addEventListener("monaco-failed", { reject(RuntimeException("Failed to load the Monaco editor.")) })

	val script = document.createElement("script") as HTMLScriptElement
	script.type = "module"
	script.textContent = """
		import('$MONACO_BASE/monaco.js')
			.then(module => { globalThis.monaco = module; window.dispatchEvent(new Event('monaco-ready')) })
			.catch(() => window.dispatchEvent(new Event('monaco-failed')))
	""".trimIndent()
	document.head!!.appendChild(script)
}.also { loading = it }
