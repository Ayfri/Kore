@file:JsModule("marked")
@file:JsNonModule
@file:JsQualifier("marked")

package io.github.ayfri.kore.website.externals

external fun use(vararg options: MarkedOptions)

external fun parse(markdown: String): String

/** Splits [markdown] into tokens without rendering them, so code fences can be pulled out before parsing. */
external fun lexer(markdown: String): Array<MarkedToken>

/** Renders tokens produced by [lexer] back to HTML. */
external fun parser(tokens: Array<MarkedToken>): String

external interface MarkedToken {
	val type: String
	val text: String

	/** Info string of a fenced code block, absent for every other token type. */
	val lang: String?
}

external interface MarkedOptions {
	var renderer: TextRenderer?
}

external interface Renderer<T> {
	fun link(href: String?, title: String?, text: String): String
	fun image(href: String?, title: String?, text: String): String
	fun code(code: String, infoString: String, escaped: Boolean): String
}

open external class TextRenderer : Renderer<String> {
	override fun link(href: String?, title: String?, text: String): String
	override fun image(href: String?, title: String?, text: String): String
	override fun code(code: String, infoString: String, escaped: Boolean): String
}
