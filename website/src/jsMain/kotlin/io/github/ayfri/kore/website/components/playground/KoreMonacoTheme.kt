package io.github.ayfri.kore.website.components.playground

import io.github.ayfri.kore.website.CodeThemeStyle
import io.github.ayfri.kore.website.externals.monaco.MonacoEditor
import io.github.ayfri.kore.website.externals.monaco.ThemeData
import io.github.ayfri.kore.website.externals.monaco.TokenThemeRule
import org.jetbrains.compose.web.css.CSSColorValue

const val MONACO_THEME_NAME = "kore-dark"

/** Monaco wants bare `RRGGBB` for token rules, the stylesheet colors are `#rrggbb`. */
private fun CSSColorValue.hex() = toString().removePrefix("#")

private fun rule(token: String, color: CSSColorValue, fontStyle: String? = null) =
	(js("({})").unsafeCast<TokenThemeRule>()).apply {
		this.token = token
		foreground = color.hex()
		if (fontStyle != null) this.fontStyle = fontStyle
	}

/**
 * The site's code theme, token for token with [CodeThemeStyle], so the editor reads like every other snippet on the
 * site. The background is the pane surface (`--landing-surface-2`), which Monaco cannot read from CSS.
 */
fun defineKoreTheme(editor: MonacoEditor) {
	val colors = js("({})")
	colors["editor.background"] = "#141c26"
	colors["editor.foreground"] = "#dde3ea"
	colors["editorLineNumber.foreground"] = "#4b5263"
	colors["editorLineNumber.activeForeground"] = "#a6b4bd"
	colors["editorCursor.foreground"] = "#1fd2f2"
	colors["editor.lineHighlightBackground"] = "#ffffff0b"
	colors["editor.lineHighlightBorder"] = "#00000000"
	colors["editor.selectionBackground"] = "#82aaff40"
	colors["editor.inactiveSelectionBackground"] = "#82aaff20"
	colors["editor.wordHighlightBackground"] = "#82aaff1a"
	colors["editor.findMatchBackground"] = "#fec90740"
	colors["editor.findMatchHighlightBackground"] = "#fec90720"
	colors["editorGutter.background"] = "#141c26"
	colors["editorIndentGuide.background1"] = "#ffffff17"
	colors["editorIndentGuide.activeBackground1"] = "#08b6d680"
	colors["editorBracketMatch.background"] = "#89ddff1a"
	colors["editorBracketMatch.border"] = "#89ddff80"
	colors["editorError.foreground"] = "#ff6b7f"
	colors["editorWarning.foreground"] = "#ffcb6b"
	colors["scrollbar.shadow"] = "#00000000"
	colors["scrollbarSlider.background"] = "#ffffff14"
	colors["scrollbarSlider.hoverBackground"] = "#ffffff24"
	colors["scrollbarSlider.activeBackground"] = "#ffffff33"
	colors["editorWidget.background"] = "#151c26"
	colors["editorWidget.border"] = "#97b0ca2e"
	colors["editorHoverWidget.background"] = "#151c26"
	colors["editorHoverWidget.border"] = "#97b0ca2e"
	colors["editorSuggestWidget.background"] = "#151c26"
	colors["editorSuggestWidget.border"] = "#97b0ca2e"
	colors["editorSuggestWidget.selectedBackground"] = "#08b6d624"
	colors["editorStickyScroll.background"] = "#141c26"
	colors["editorStickyScroll.shadow"] = "#00000080"
	colors["editorStickyScrollHover.background"] = "#1b2531"
	colors["editorWhitespace.foreground"] = "#ffffff24"
	colors["descriptionForeground"] = "#a6b4bd"
	colors["focusBorder"] = "#08b6d680"
	colors["input.background"] = "#0f141b"
	colors["input.border"] = "#97b0ca2e"
	colors["input.placeholderForeground"] = "#a6b4bd80"
	colors["keybindingLabel.background"] = "#ffffff0f"
	colors["keybindingLabel.border"] = "#97b0ca2e"
	colors["keybindingLabel.bottomBorder"] = "#97b0ca2e"
	colors["keybindingLabel.foreground"] = "#dde3ea"
	colors["list.focusOutline"] = "#00000000"
	colors["list.highlightForeground"] = "#1fd2f2"
	colors["list.hoverBackground"] = "#ffffff0d"
	colors["menu.background"] = "#151c26"
	colors["menu.border"] = "#97b0ca2e"
	colors["menu.foreground"] = "#dde3ea"
	colors["menu.selectionBackground"] = "#08b6d633"
	colors["menu.separatorBackground"] = "#97b0ca2e"
	colors["minimap.background"] = "#141c26"
	colors["pickerGroup.border"] = "#97b0ca2e"
	colors["pickerGroup.foreground"] = "#08b6d6"
	colors["progressBar.background"] = "#1fd2f2"
	colors["quickInput.background"] = "#121922"
	colors["quickInput.foreground"] = "#dde3ea"
	colors["quickInputList.focusBackground"] = "#08b6d62e"
	colors["quickInputList.focusForeground"] = "#f7f9fc"
	colors["quickInputList.focusHighlightForeground"] = "#1fd2f2"
	colors["widget.border"] = "#97b0ca2e"
	colors["widget.shadow"] = "#00000080"

	val themeData = (js("({})").unsafeCast<ThemeData>()).apply {
		base = "vs-dark"
		inherit = false
		this.colors = colors
		rules = with(CodeThemeStyle) {
			arrayOf(
				rule("", textColor),
				rule("identifier", textColor),

				rule("comment", commentColor, "italic"),
				rule("comment.doc", commentColor, "italic"),

				rule("keyword", keywordColor, "italic"),
				rule("keyword.control", keywordColor, "italic"),
				rule("storage.modifier", keywordColor, "italic"),
				rule("annotation", keywordColor, "italic"),

				rule("keyword.type", classColor),
				rule("type", classColor),
				rule("type.identifier", classColor),
				rule("storage.type", classColor),

				rule("identifier.function", functionColor),
				rule("function", functionColor),
				rule("predefined", functionColor),

				rule("constant", numberColor),
				rule("number", numberColor),
				rule("number.float", numberColor),
				rule("number.hex", numberColor),

				rule("string", stringColor),
				rule("string.escape", punctuationColor),
				rule("string.escape.invalid", propertyColor),

				rule("operator", punctuationColor),
				rule("delimiter", punctuationColor),
				rule("delimiter.parenthesis", punctuationColor),
				rule("delimiter.bracket", punctuationColor),
				rule("delimiter.curly", punctuationColor),
				rule("delimiter.square", punctuationColor),
				rule("delimiter.angle", punctuationColor),

				rule("tag", propertyColor),
				rule("attribute.name", classColor, "italic"),
				rule("attribute.value", stringColor),
			)
		}
	}

	editor.defineTheme(MONACO_THEME_NAME, themeData)
}
