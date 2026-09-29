package io.github.ayfri.kore.website.components.playground

import io.github.ayfri.kore.website.externals.monaco.EditorOptions
import io.github.ayfri.kore.website.utils.jsObject

/** Editor, build and preview preferences from the settings panel, kept across visits. */
object PlaygroundSettings {
	val FONT_SIZES = 11..22
	val JSON_INDENTS = listOf(2, 4)
	private const val DEFAULT_FONT_SIZE = 14
	private const val DEFAULT_JSON_INDENT = 4

	/** Type-check and rebuild once typing pauses. Off, only Run compiles, and the squiggles still follow the buffer. */
	var autoBuild by persisted("autoBuild", true)
	var fontLigatures by persisted("ligatures", false)
	var fontSize by persisted("fontSize", DEFAULT_FONT_SIZE, FONT_SIZES)

	/** Spaces per level of pretty-printed JSON in the preview, 4 by default to match the editor's tab size and the indent guides. */
	var jsonIndent by Persisted("jsonIndent", DEFAULT_JSON_INDENT) { stored -> stored.toIntOrNull()?.takeIf { it in JSON_INDENTS } }
	var lineNumbers by persisted("lineNumbers", true)
	var minimap by persisted("minimap", false)
	var prettyJson by persisted("pretty", true)
	var previewWrap by persisted("previewWrap", false)
	var renderWhitespace by persisted("whitespace", false)
	var stickyScroll by persisted("stickyScroll", true)
	var wordWrap by persisted("wordWrap", false)

	fun reset() {
		autoBuild = true
		fontLigatures = false
		fontSize = DEFAULT_FONT_SIZE
		jsonIndent = DEFAULT_JSON_INDENT
		lineNumbers = true
		minimap = false
		prettyJson = true
		previewWrap = false
		renderWhitespace = false
		stickyScroll = true
		wordWrap = false
	}

	/** Writes the Monaco options these settings drive, at creation and through `updateOptions` on every change. */
	fun applyTo(options: EditorOptions): EditorOptions {
		options.fontLigatures = fontLigatures
		options.fontSize = fontSize
		options.lineNumbers = if (lineNumbers) "on" else "off"
		options.minimap = jsObject { enabled = minimap }
		options.renderWhitespace = if (renderWhitespace) "all" else "selection"
		options.stickyScroll = jsObject { enabled = stickyScroll }
		options.wordWrap = if (wordWrap) "on" else "off"
		return options
	}
}
