package io.github.ayfri.kore.website.components.playground

import io.github.ayfri.kore.website.externals.monaco.MonacoEditor
import io.github.ayfri.kore.website.externals.monaco.ThemeData
import io.github.ayfri.kore.website.externals.monaco.TokenThemeRule

const val MONACO_THEME_NAME = "material-darker"

private fun rule(token: String, foreground: String, fontStyle: String? = null) =
	(js("({})").unsafeCast<TokenThemeRule>()).apply {
		this.token = token
		this.foreground = foreground
		if (fontStyle != null) this.fontStyle = fontStyle
	}

/**
 * Material Darker, matching the IntelliJ IDEA theme of the same name. Token colours line up with
 * [io.github.ayfri.kore.website.CodeThemeStyle] so playground code reads identically to the Prism-highlighted
 * snippets in the docs.
 */
fun defineMaterialDarkerTheme(editor: MonacoEditor) {
	val colors = js("({})")
	colors["editor.background"] = "#212121"
	colors["editor.foreground"] = "#EEFFFF"
	colors["editorLineNumber.foreground"] = "#424242"
	colors["editorLineNumber.activeForeground"] = "#616161"
	colors["editorCursor.foreground"] = "#FFCC00"
	colors["editor.lineHighlightBackground"] = "#181818"
	colors["editor.lineHighlightBorder"] = "#00000000"
	colors["editor.selectionBackground"] = "#353535"
	colors["editor.inactiveSelectionBackground"] = "#29292980"
	colors["editor.wordHighlightBackground"] = "#35353580"
	colors["editor.wordHighlightBorder"] = "#89DDFF40"
	colors["editor.findMatchBackground"] = "#FFCC0040"
	colors["editor.findMatchHighlightBackground"] = "#FFCC0025"
	colors["editorGutter.background"] = "#212121"
	colors["editorIndentGuide.background1"] = "#424242"
	colors["editorIndentGuide.activeBackground1"] = "#FF9800"
	colors["editorBracketMatch.background"] = "#89DDFF20"
	colors["editorBracketMatch.border"] = "#89DDFF"
	colors["scrollbar.shadow"] = "#00000000"
	colors["scrollbarSlider.background"] = "#61616180"
	colors["scrollbarSlider.hoverBackground"] = "#616161C0"
	colors["scrollbarSlider.activeBackground"] = "#616161"
	colors["editorWidget.background"] = "#292929"
	colors["editorSuggestWidget.background"] = "#292929"
	colors["editorSuggestWidget.border"] = "#424242"
	colors["editorSuggestWidget.selectedBackground"] = "#353535"
	colors["minimap.background"] = "#1A1A1A"

	val themeData = (js("({})").unsafeCast<ThemeData>()).apply {
		base = "vs-dark"
		inherit = false
		this.colors = colors
		rules = arrayOf(
			rule("", "EEFFFF"),
			rule("identifier", "EEFFFF"),

			rule("comment", "616161", "italic"),
			rule("comment.doc", "616161", "italic"),

			rule("keyword", "C792EA", "italic"),
			rule("keyword.control", "C792EA", "italic"),
			rule("storage.modifier", "C792EA", "italic"),
			rule("annotation", "C792EA"),
			rule("meta.preprocessor", "C792EA", "italic"),

			rule("keyword.type", "FFCB6B"),
			rule("type", "FFCB6B"),
			rule("type.identifier", "FFCB6B"),
			rule("storage.type", "FFCB6B"),

			rule("identifier.function", "82AAFF"),
			rule("function", "82AAFF"),
			rule("support.function", "82AAFF"),
			rule("entity.name.function", "82AAFF"),
			rule("predefined", "82AAFF", "italic"),
			rule("variable.predefined", "82AAFF", "italic"),

			rule("variable.parameter", "F78C6C"),
			rule("constant", "F78C6C"),
			rule("constant.numeric", "F78C6C"),
			rule("constant.language", "FF5370"),
			rule("number", "F78C6C"),
			rule("number.float", "F78C6C"),
			rule("number.hex", "F78C6C"),

			rule("string", "C3E88D"),
			rule("string.quoted", "C3E88D"),
			rule("string.escape", "89DDFF"),
			rule("string.escape.invalid", "FF5370"),

			rule("operator", "89DDFF"),
			rule("delimiter", "89DDFF"),
			rule("delimiter.parenthesis", "89DDFF"),
			rule("delimiter.bracket", "89DDFF"),
			rule("delimiter.curly", "89DDFF"),
			rule("delimiter.square", "89DDFF"),
			rule("delimiter.angle", "89DDFF"),
			rule("metatag", "89DDFF"),

			rule("tag", "F07178"),
			rule("attribute.name", "FFCB6B", "italic"),
			rule("attribute.value", "C3E88D"),
		)
	}

	editor.defineTheme(MONACO_THEME_NAME, themeData)
}
