package io.github.ayfri.kore.website.externals.monaco

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLScriptElement
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
	val languages: MonacoLanguages
}

external interface MonacoEditor {
	fun create(domElement: HTMLElement, options: EditorOptions): CodeEditor
	fun defineTheme(themeName: String, themeData: ThemeData)
	fun setTheme(themeName: String)
	fun setModelMarkers(model: TextModel, owner: String, markers: Array<MarkerData>)
}

external interface MonacoLanguages {
	fun registerCompletionItemProvider(languageSelector: String, provider: CompletionItemProvider): Disposable
}

external interface Disposable {
	fun dispose()
}

external interface TextModel

external interface Position {
	val lineNumber: Int
	val column: Int
}

external interface CodeEditor : Disposable {
	fun getValue(): String
	fun setValue(newValue: String)
	fun getModel(): TextModel?
	fun getPosition(): Position?
	fun setPosition(position: Position)
	fun focus()
	fun layout()
	fun revealLineInCenter(lineNumber: Int)
	fun onDidChangeModelContent(listener: () -> Unit): Disposable
}

/**
 * Only the options the playground actually sets. Monaco accepts far more; add fields here as needed rather
 * than reaching for `dynamic`.
 */
external interface EditorOptions {
	var automaticLayout: Boolean?
	var bracketPairColorization: BracketPairColorizationOptions?
	var fontFamily: String?
	var fontLigatures: Boolean?
	var fontSize: Int?
	var guides: GuidesOptions?
	var insertSpaces: Boolean?
	var language: String?
	var minimap: MinimapOptions?
	var padding: PaddingOptions?
	var renderWhitespace: String?
	var scrollBeyondLastLine: Boolean?
	var tabSize: Int?
	var theme: String?
	var value: String?
	var wordWrap: String?
}

external interface BracketPairColorizationOptions {
	var enabled: Boolean?
}

external interface GuidesOptions {
	var bracketPairs: Boolean?
	var highlightActiveBracketPair: Boolean?
	var highlightActiveIndentation: Boolean?
	var indentation: Boolean?
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
	var colors: dynamic
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

external interface CompletionItemProvider {
	var triggerCharacters: Array<String>?
	val provideCompletionItems: (model: TextModel, position: Position) -> dynamic
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
fun loadMonaco(): Promise<Monaco> = loading ?: Promise<Monaco> { resolve, reject ->
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
