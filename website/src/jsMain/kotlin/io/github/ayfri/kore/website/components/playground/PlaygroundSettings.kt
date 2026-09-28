package io.github.ayfri.kore.website.components.playground

import io.github.ayfri.kore.website.externals.monaco.EditorOptions
import io.github.ayfri.kore.website.externals.monaco.MinimapOptions
import io.github.ayfri.kore.website.externals.monaco.StickyScrollOptions

/** Editor, build and preview preferences from the settings panel, kept across visits. */
object PlaygroundSettings {
	val FONT_SIZES = 11..22
	private const val DEFAULT_FONT_SIZE = 14

	/** Type-check and rebuild once typing pauses. Off, only Run compiles, and the squiggles still follow the buffer. */
	var autoBuild by persisted("autoBuild", true)
	var fontLigatures by persisted("ligatures", false)
	var fontSize by persisted("fontSize", DEFAULT_FONT_SIZE, FONT_SIZES)
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
		options.minimap = js("({})").unsafeCast<MinimapOptions>().also { it.enabled = minimap }
		options.renderWhitespace = if (renderWhitespace) "all" else "selection"
		options.stickyScroll = js("({})").unsafeCast<StickyScrollOptions>().also { it.enabled = stickyScroll }
		options.wordWrap = if (wordWrap) "on" else "off"
		return options
	}
}
