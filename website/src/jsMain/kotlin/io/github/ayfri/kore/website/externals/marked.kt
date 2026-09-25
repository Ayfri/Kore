@file:JsModule("marked")
@file:JsNonModule
@file:JsQualifier("marked")

package io.github.ayfri.kore.website.externals

external fun use(vararg extensions: MarkedExtension)

external fun parse(markdown: String): String

/** Splits [markdown] into tokens without rendering them, so code fences can be pulled out before parsing. */
external fun lexer(markdown: String): Array<MarkedToken>

/** Renders tokens produced by [lexer] back to HTML. */
external fun parser(tokens: Array<MarkedToken>): String

external interface MarkedToken {
	val type: String
	val text: String

	/** Target of a link or image token, absent for every other token type. */
	var href: String?

	/** Info string of a fenced code block, absent for every other token type. */
	val lang: String?
}

external interface MarkedExtension {
	var renderer: MarkedRenderer?
}

/**
 * Renderer overrides, read with `for...in`, so they must be own properties of a plain object (see `jsObject`), never class methods.
 * An override returning `false` falls back to the default renderer.
 */
external interface MarkedRenderer {
	/** Raw HTML found in the markdown, block or inline. */
	var html: ((token: MarkedToken) -> String)?
	var image: ((token: MarkedToken) -> Boolean)?
	var link: ((token: MarkedToken) -> Boolean)?
}
