package io.github.ayfri.kore.website.components.playground

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import io.github.ayfri.kore.website.externals.monaco.CodeEditor
import io.github.ayfri.kore.website.externals.monaco.EditorOptions
import io.github.ayfri.kore.website.externals.monaco.loadMonaco
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

private fun editorOptions(value: String): EditorOptions = (js("({})").unsafeCast<EditorOptions>()).apply {
	automaticLayout = true
	bracketPairColorization = js("({ enabled: true })")
	fontFamily = "'JetBrains Mono', 'Fira Code', 'Cascadia Code', Consolas, monospace"
	fontLigatures = true
	fontSize = 14
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

/**
 * Monaco is loaded and created after mount, never during composition, and disposed on teardown.
 *
 * It is skipped entirely while `kobweb export` snapshots the site: the export runs the page in a headless
 * browser where Monaco's web workers fail, which crashes the route. The exported HTML only needs the empty
 * container - real visitors hydrate the editor client-side.
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
	val currentOnChange by rememberUpdatedState(onChange)
	val currentOnReady by rememberUpdatedState(onReady)

	Div({
		classes(className)
		ref {
			container = it
			onDispose { container = null }
		}
	})

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
			currentOnReady(created)
		}

		onDispose {
			disposed = true
			changeSubscription?.dispose()
			instance?.dispose()
		}
	}
}
