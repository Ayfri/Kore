package io.github.ayfri.kore.website.components.playground

import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.externals.monaco.MonacoEditor
import io.github.ayfri.kore.website.externals.monaco.TokenThemeRule
import io.github.ayfri.kore.website.utils.jsObject
import org.jetbrains.compose.web.css.CSSColorValue
import kotlin.js.json

const val MONACO_THEME_NAME = "kore-dark"

private const val ACCENT = "#1fd2f2"
private const val BORDER = "#97b0ca2e"
private const val SURFACE = "#141c26"
private const val WIDGET = "#151c26"

/** Monaco wants bare `RRGGBB` for token rules, the stylesheet colors are `#rrggbb`. */
private fun CSSColorValue.hex() = toString().removePrefix("#")

private fun rule(token: String, color: CSSColorValue, fontStyle: String? = null) = jsObject<TokenThemeRule> {
	this.token = token
	foreground = color.hex()
	if (fontStyle != null) this.fontStyle = fontStyle
}

/**
 * Styles the tokens of `website/monaco/kotlin-grammar.mjs` like IntelliJ's Material Darker scheme, in the colors of
 * [CodeThemeStyle]. Calls are italic like its top-level and extension calls, which make up most Kore code, member calls
 * can't be told apart without resolving. The background is the pane surface (`--landing-surface-2`), which Monaco
 * cannot read from CSS.
 */
fun defineKoreTheme(editor: MonacoEditor) = editor.defineTheme(MONACO_THEME_NAME, jsObject {
	val text = CodeThemeStyle.textColor.toString()

	base = "vs-dark"
	colors = json(
		"descriptionForeground" to "#a6b4bd",
		"editor.background" to SURFACE,
		"editor.findMatchBackground" to "#fec90740",
		"editor.findMatchHighlightBackground" to "#fec90720",
		"editor.foreground" to text,
		"editor.inactiveSelectionBackground" to "#82aaff20",
		"editor.lineHighlightBackground" to "#ffffff0b",
		"editor.lineHighlightBorder" to "#00000000",
		"editor.selectionBackground" to "#82aaff40",
		"editor.wordHighlightBackground" to "#82aaff1a",
		"editorBracketMatch.background" to "#89ddff1a",
		"editorBracketMatch.border" to "#89ddff80",
		"editorCursor.foreground" to ACCENT,
		"editorError.foreground" to "#ff6b7f",
		"editorGutter.background" to SURFACE,
		"editorHoverWidget.background" to WIDGET,
		"editorHoverWidget.border" to BORDER,
		"editorIndentGuide.activeBackground1" to "#08b6d680",
		"editorIndentGuide.background1" to "#ffffff17",
		"editorLineNumber.activeForeground" to "#a6b4bd",
		"editorLineNumber.foreground" to "#4b5263",
		"editorStickyScroll.background" to SURFACE,
		"editorStickyScroll.shadow" to "#00000080",
		"editorStickyScrollHover.background" to "#1b2531",
		"editorSuggestWidget.background" to WIDGET,
		"editorSuggestWidget.border" to BORDER,
		"editorSuggestWidget.selectedBackground" to "#08b6d624",
		"editorWarning.foreground" to "#ffcb6b",
		"editorWhitespace.foreground" to "#ffffff24",
		"editorWidget.background" to WIDGET,
		"editorWidget.border" to BORDER,
		"focusBorder" to "#08b6d680",
		"input.background" to "#0f141b",
		"input.border" to BORDER,
		"input.placeholderForeground" to "#a6b4bd80",
		"keybindingLabel.background" to "#ffffff0f",
		"keybindingLabel.border" to BORDER,
		"keybindingLabel.bottomBorder" to BORDER,
		"keybindingLabel.foreground" to text,
		"list.focusOutline" to "#00000000",
		"list.highlightForeground" to ACCENT,
		"list.hoverBackground" to "#ffffff0d",
		"menu.background" to WIDGET,
		"menu.border" to BORDER,
		"menu.foreground" to text,
		"menu.selectionBackground" to "#08b6d633",
		"menu.separatorBackground" to BORDER,
		"minimap.background" to SURFACE,
		"pickerGroup.border" to BORDER,
		"pickerGroup.foreground" to "#08b6d6",
		"progressBar.background" to ACCENT,
		"quickInput.background" to "#121922",
		"quickInput.foreground" to text,
		"quickInputList.focusBackground" to "#08b6d62e",
		"quickInputList.focusForeground" to "#f7f9fc",
		"quickInputList.focusHighlightForeground" to ACCENT,
		"scrollbar.shadow" to "#00000000",
		"scrollbarSlider.activeBackground" to "#ffffff33",
		"scrollbarSlider.background" to "#ffffff14",
		"scrollbarSlider.hoverBackground" to "#ffffff24",
		"widget.border" to BORDER,
		"widget.shadow" to "#00000080",
	)
	inherit = false
	rules = with(CodeThemeStyle) {
		arrayOf(
			rule("", textColor),
			rule("identifier", textColor),

			rule("comment", commentColor, "italic"),

			rule("keyword", keywordColor, "italic"),
			rule("annotation", keywordColor),

			rule("type", classColor),
			rule("constant", textColor, "italic"),

			rule("function", functionColor),
			rule("function.call", functionColor, "italic"),

			rule("parameter", numberColor),
			rule("parameter.implicit", numberColor, "bold"),

			rule("number", numberColor),

			rule("string", stringColor),
			rule("string.escape", punctuationColor),
			rule("string.escape.invalid", propertyColor),

			rule("delimiter", punctuationColor),
		)
	}
})
