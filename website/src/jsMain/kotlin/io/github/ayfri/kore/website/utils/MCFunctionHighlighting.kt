package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.externals.grammar
import io.github.ayfri.kore.website.externals.set
import io.github.ayfri.kore.website.externals.token
import kotlin.js.RegExp

private const val BOOLEAN = "\\b(?:true|false|True|False)\\b"
private const val ELLIPSIS_OR_RANGE = "\\.\\.\\.?"
private const val ESCAPED_STRING = "\"(?:\\\\.|[^\\\\\"\\r\\n])*\""
private const val NUMBER = "[+-]?(?:\\d+(?:\\.\\d+)?|\\.\\d+)(?:[eE][+-]?\\d+)?[bBsSlLfFdD]?(?!\\w)"
private const val QUOTED_STRING = "$ESCAPED_STRING|'(?:\\\\.|[^\\\\'\\r\\n])*'"
private const val UNQUOTED = "[^{}\\[\\]\"']"

/** A `{...}` or `[...]` group nests as deep as the real commands go, with strings skipped whole so their brackets never count. */
private fun balanced(depth: Int) = (1..depth).fold("$UNQUOTED|$QUOTED_STRING") { inner, _ ->
	"$UNQUOTED|$QUOTED_STRING|\\{(?:$inner)*\\}|\\[(?:$inner)*\\]"
}

private val GROUP_BODY = balanced(4)
private val BRACKETS = "\\[(?:$GROUP_BODY)*\\]"
private val COMPOUND = "\\{(?:$GROUP_BODY)*\\}"

/** Numbers and IDs only start on their own, so `isible:1b` or `distance2` never match inside a longer word. */
private val EMBEDDED_NUMBER = "(?<![\\w.])$NUMBER"
private val RESOURCE_NAME = "(?<![\\w.:/-])#?[a-z_][a-z0-9_\\.-]*:[a-z0-9_\\.\\/-]+"

/** A fresh instance per use: Prism rewrites a greedy token's pattern in place the first time it runs. */
private fun quotedString() = token(RegExp(QUOTED_STRING), greedy = true)

private fun quotedKey() = token(RegExp("(?:$QUOTED_STRING)(?=\\s*[:=])"), greedy = true, alias = "property")

private fun resourceName() = token(RegExp(RESOURCE_NAME), alias = "function")

/** SNBT and JSON compounds and lists, `{CustomNameVisible:1b,scale:[2.0f]}` or `{"text":"hi"}`: keys are properties, `1b` is one number. */
private fun compoundBody() = grammar(
	"quotedKey" to quotedKey(),
	"string" to quotedString(),
	"property" to RegExp("[\\w.+-]+(?=\\s*[:=])"),
	"keyword" to RegExp("(?<=\\[)[BIL](?=;)"),
	"operator" to RegExp(ELLIPSIS_OR_RANGE),
	"number" to RegExp(EMBEDDED_NUMBER),
	"boolean" to RegExp(BOOLEAN),
	"punctuation" to RegExp("[{}\\[\\](),;:=]"),
)

/** Contents of `[...]` after an ID or in a JSON text list: block states, item components, NBT path filters. */
private fun bracketBody() = grammar(
	"string" to quotedString(),
	"compound" to token(RegExp(COMPOUND), greedy = true, inside = compoundBody()),
	"property" to RegExp("[\\w.:/+-]+(?=\\s*=)"),
	"resourceName" to resourceName(),
	"operator" to RegExp("$ELLIPSIS_OR_RANGE|[=!]"),
	"number" to RegExp(EMBEDDED_NUMBER),
	"boolean" to RegExp(BOOLEAN),
	"punctuation" to RegExp("[{}\\[\\](),;:]"),
)

/** Contents of an `@a[...]` target selector. */
private fun selectorArguments() = grammar(
	"string" to quotedString(),
	"compound" to token(RegExp(COMPOUND), greedy = true, inside = compoundBody()),
	"selector-tag" to token(RegExp("\\w+(?=\\s*=)"), alias = "keyword"),
	"boolean" to RegExp(BOOLEAN),
	"uuid" to RegExp("\\b[0-9a-fA-F]+(?:-[0-9a-fA-F]+){4}\\b"),
	"resourceName" to resourceName(),
	"operator" to RegExp("$ELLIPSIS_OR_RANGE|[=!,]"),
	"number" to RegExp(EMBEDDED_NUMBER),
	"punctuation" to RegExp("[\\[\\]]"),
)

/**
 * Registers the `mcfunction` grammar. Token order is matching order, so the most specific ones come first: strings, selectors,
 * compounds and brackets are greedy and run before the word-level tokens, which would otherwise match inside them.
 */
fun initMCFunctionHighlighting() {
	Prism.languages["mcfunction"] = grammar(
		"comment" to token(RegExp("^\\s*#.*$", "m"), greedy = true),
		"command" to token(RegExp("^\\s*([a-z_]+)(?=\\s|$)", "m"), alias = "keyword"),
		"string" to token(RegExp(ESCAPED_STRING), greedy = true),
		"selector" to token(RegExp("@[aenprs](?:$BRACKETS)?"), greedy = true, alias = "class-name", inside = selectorArguments()),
		"compound" to token(RegExp(COMPOUND), greedy = true, inside = compoundBody()),
		"brackets" to token(RegExp(BRACKETS), greedy = true, inside = bracketBody()),
		"resourceName" to resourceName(),
		"range" to token(RegExp(ELLIPSIS_OR_RANGE), alias = "operator"),
		"coordinate" to token(RegExp("[~^]|(?<!\\w)$NUMBER"), alias = "number"),
		"operator" to token(RegExp("[\\-%?!+*<>\\/|&=.:,;]"), alias = "operator"),
		"property" to RegExp("[\\w\\-.]+"),
		"punctuation" to RegExp("[\\[\\](){}:,]"),
	)
}
