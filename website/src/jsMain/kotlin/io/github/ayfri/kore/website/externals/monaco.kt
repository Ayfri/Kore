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
	val languages: MonacoLanguages

	/** `monaco.KeyCode`, a numeric enum whose names match `KeyboardEvent.code` (`Enter`, `KeyS`...). */
	@Suppress("PropertyName")
	val KeyCode: Any

	@Suppress("PropertyName")
	val KeyMod: KeyMods
}

@Suppress("PropertyName")
external interface KeyMods {
	val Alt: Int
	val CtrlCmd: Int
	val Shift: Int
}

external interface MonacoEditor {
	fun create(domElement: HTMLElement, options: EditorOptions): CodeEditor
	fun defineTheme(themeName: String, themeData: ThemeData)
	fun getModelMarkers(filter: MarkerFilter): Array<MarkerData>
	fun setModelMarkers(model: TextModel, owner: String, markers: Array<MarkerData>)
}

external interface MarkerFilter {
	var owner: String?
}

/** `monaco.languages`: the providers the playground registers for Kotlin, and the runtime enums their results use. */
@Suppress("PropertyName")
external interface MonacoLanguages {
	val CompletionItemInsertTextRule: CompletionItemInsertTextRules
	val CompletionItemKind: CompletionItemKinds
	val CompletionItemTag: CompletionItemTags

	fun registerCodeActionProvider(languageId: String, provider: CodeActionProvider, metadata: CodeActionProviderMetadata): Disposable
	fun registerCompletionItemProvider(languageId: String, provider: CompletionItemProvider): Disposable
	fun registerHoverProvider(languageId: String, provider: HoverProvider): Disposable
	fun registerSignatureHelpProvider(languageId: String, provider: SignatureHelpProvider): Disposable
}

@Suppress("PropertyName")
external interface CompletionItemInsertTextRules {
	val InsertAsSnippet: Int
}

@Suppress("PropertyName")
external interface CompletionItemKinds {
	val Class: Int
	val Constant: Int
	val Enum: Int
	val EnumMember: Int
	val Function: Int
	val Interface: Int
	val Keyword: Int
	val Method: Int
	val Module: Int
	val Property: Int
	val TypeParameter: Int
	val Variable: Int
}

@Suppress("PropertyName")
external interface CompletionItemTags {
	val Deprecated: Int
}

external interface MarkdownString {
	var value: String
}

/** Providers below receive Monaco's context and cancellation token as `Any?`: they answer fast enough to never check them. */
external interface CompletionItemProvider {
	var provideCompletionItems: (model: TextModel, position: Position, context: Any?, token: Any?) -> Promise<CompletionList?>
	var resolveCompletionItem: ((item: CompletionItem, token: Any?) -> CompletionItem)?
	var triggerCharacters: Array<String>?
}

external interface CompletionList {
	var incomplete: Boolean?
	var suggestions: Array<CompletionItem>
}

/** `label` shows `detail` right after the name and `description` right-aligned, the way IntelliJ lays out a lookup. */
external interface CompletionItemLabel {
	var description: String?
	var detail: String?
	var label: String
}

external interface CompletionItem {
	var additionalTextEdits: Array<EditOperation>?
	var command: EditorCommand?
	var detail: String?
	var documentation: MarkdownString?
	var filterText: String?
	var insertText: String
	var insertTextRules: Int?
	var kind: Int
	var label: CompletionItemLabel
	var range: Range
	var sortText: String?
	var tags: Array<Int>?
}

external interface EditorCommand {
	var id: String
	var title: String
}

external interface Hover {
	var contents: Array<MarkdownString>
	var range: Range?
}

external interface HoverProvider {
	var provideHover: (model: TextModel, position: Position, token: Any?) -> Promise<Hover?>
}

external interface ParameterInformation {
	var documentation: MarkdownString?

	/** Start and end offsets in the signature label, unambiguous where a parameter name also appears in a type. */
	var label: Array<Int>
}

external interface SignatureInformation {
	var documentation: MarkdownString?
	var label: String
	var parameters: Array<ParameterInformation>
}

external interface SignatureHelp {
	var activeParameter: Int
	var activeSignature: Int
	var signatures: Array<SignatureInformation>
}

external interface SignatureHelpResult : Disposable {
	var value: SignatureHelp
}

external interface SignatureHelpProvider {
	var provideSignatureHelp: (model: TextModel, position: Position, token: Any?, context: Any?) -> Promise<SignatureHelpResult?>
	var signatureHelpRetriggerCharacters: Array<String>?
	var signatureHelpTriggerCharacters: Array<String>?
}

external interface CodeActionContext {
	val markers: Array<MarkerData>
	val only: String?
}

external interface TextEdit {
	var range: Range
	var text: String
}

external interface WorkspaceTextEdit {
	var resource: Any
	var textEdit: TextEdit
	var versionId: Int?
}

external interface WorkspaceEdit {
	var edits: Array<WorkspaceTextEdit>
}

external interface CodeAction {
	var diagnostics: Array<MarkerData>?
	var edit: WorkspaceEdit?
	var isPreferred: Boolean?
	var kind: String?
	var title: String
}

external interface CodeActionList : Disposable {
	var actions: Array<CodeAction>
}

external interface CodeActionProvider {
	var provideCodeActions: (model: TextModel, range: Range, context: CodeActionContext, token: Any?) -> Promise<CodeActionList?>
}

external interface CodeActionProviderMetadata {
	var providedCodeActionKinds: Array<String>?
}

external interface Disposable {
	fun dispose()
}

external interface WordAtPosition {
	val endColumn: Int
	val startColumn: Int
	val word: String
}

external interface TextModel {
	/** The model's `monaco.Uri`, only ever handed back to Monaco in edits. */
	val uri: Any

	fun getFullModelRange(): Range
	fun getOffsetAt(position: Position): Int
	fun getValue(): String
	fun getValueLengthInRange(range: Range): Int
	fun getVersionId(): Int
	fun getWordAtPosition(position: Position): WordAtPosition?
	fun getWordUntilPosition(position: Position): WordAtPosition
}

external interface Position {
	var column: Int
	var lineNumber: Int
}

external interface Range {
	var endColumn: Int
	var endLineNumber: Int
	var startColumn: Int
	var startLineNumber: Int
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

	// The worker is bundled as a classic (IIFE) script, so it starts straight from its URL: same origin, no module
	// worker, no blob proxy. No language service is bundled, so the editor worker is the only one Monaco asks for.
	js("globalThis.MonacoEnvironment = { getWorker: function () { return new Worker('$MONACO_BASE/editor.worker.js') } }")

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
