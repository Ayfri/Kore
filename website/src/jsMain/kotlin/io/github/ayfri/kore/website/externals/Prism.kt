package io.github.ayfri.kore.website.externals

import io.github.ayfri.kore.website.utils.jsObject
import org.w3c.dom.Element
import kotlin.js.RegExp

/** Bindings for the Prism highlighter, loaded as a global script rather than as a module. */
external object Prism {
	val languages: PrismLanguages

	fun highlightAll()
	fun highlightAllUnder(element: Element)
}

/** The grammars Prism knows about, keyed by language name. */
external interface PrismLanguages {
	/** Adds [tokens] to [language] right before its [before] token, so matching order stays under control. */
	fun insertBefore(language: String, before: String, tokens: PrismGrammar)
}

operator fun PrismLanguages.get(language: String): PrismGrammar? = asDynamic()[language]

operator fun PrismLanguages.set(language: String, grammar: PrismGrammar) {
	asDynamic()[language] = grammar
}

/** An ordered map of token name to its definition, either a bare [RegExp] or a [PrismToken]. */
external interface PrismGrammar

operator fun PrismGrammar.set(token: String, definition: Any) {
	asDynamic()[token] = definition
}

/** Builds a grammar. Declaration order is matching order, so keep the most specific tokens first. */
fun grammar(vararg tokens: Pair<String, Any>) = jsObject<PrismGrammar> {
	tokens.forEach { (name, definition) -> set(name, definition) }
}

external interface PrismToken {
	var pattern: RegExp?

	/** Drops the first capture group from the match, so a pattern can require context it must not consume. */
	var lookbehind: Boolean?

	/** Lets the token match across others already tokenized, needed for strings and comments. */
	var greedy: Boolean?

	/** Extra CSS class applied on top of the token name. */
	var alias: String?

	/** Grammar applied to the token's own content. */
	var inside: PrismGrammar?
}

fun token(
	pattern: RegExp,
	lookbehind: Boolean? = null,
	greedy: Boolean? = null,
	alias: String? = null,
	inside: PrismGrammar? = null,
) = jsObject<PrismToken> {
	// Only the properties that carry a value are set, so a token looks exactly like the object literals Prism ships with.
	this.pattern = pattern
	lookbehind?.let { this.lookbehind = it }
	greedy?.let { this.greedy = it }
	alias?.let { this.alias = it }
	inside?.let { this.inside = it }
}
