package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.externals.grammar
import io.github.ayfri.kore.website.externals.set
import io.github.ayfri.kore.website.externals.token
import kotlin.js.RegExp

private const val NUMBER = "[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?[bfdl]?\\b"
private const val QUOTED_STRING = "\"[^\"]*\""
private const val ESCAPED_STRING = "\"(?:\\\\.|[^\\\\\"\\r\\n])*\""

/** A fresh instance per use: Prism rewrites a greedy token's pattern in place the first time it runs. */
private fun quotedString() = token(RegExp(QUOTED_STRING), greedy = true)

/** Contents of an `@a[...]` target selector. */
private val selectorArguments = grammar(
	"boolean" to RegExp("\\b(?:true|false|True|False)\\b"),
	"operator" to RegExp("[=!,]|\\.\\."),
	"number" to RegExp(NUMBER),
	"uuid" to RegExp("\\b[0-9a-fA-F]+(?:-[0-9a-fA-F]+){4}\\b"),
	"punctuation" to RegExp("[\\[\\]]"),
	"scores" to token(
		RegExp("scores=\\{[^}]*\\}"),
		inside = grammar(
			"punctuation" to RegExp("[{}=,]"),
			"property" to RegExp("\\w+"),
			"number" to RegExp("[+-]?\\d*\\.?\\d+"),
			"operator" to RegExp("\\.\\.|[<>=]+"),
		),
	),
	"selector-tag" to token(RegExp("\\b\\w+\\b"), alias = "keyword"),
	"string" to quotedString(),
)

private val nbtPathParts = grammar(
	"property" to RegExp("[\\w\\-.]+"),
	"string" to quotedString(),
	"number" to RegExp(NUMBER),
	"punctuation" to RegExp("[.\\[\\]]"),
)

private val jsonBody = grammar(
	"property" to token(RegExp("\"[^\"]*\"(?=\\s*:)"), greedy = true),
	"string" to token(RegExp(ESCAPED_STRING), greedy = true),
	"number" to RegExp("[+-]?\\d*\\.?\\d+([eE]?[+-]?\\d+)?\\b"),
	"punctuation" to RegExp("[{}[\\],]"),
	"operator" to RegExp(":"),
	"boolean" to RegExp("\\b(?:true|false)\\b"),
	"null" to token(RegExp("\\bnull\\b"), alias = "keyword"),
)

private val nbtBody = grammar(
	"property" to RegExp("[\\w\\-.]+"),
	"punctuation" to RegExp("[{},:]"),
	"string" to quotedString(),
	"number" to RegExp(NUMBER),
	"boolean" to RegExp("\\b(?:true|false|True|False)\\b"),
	"array" to token(
		RegExp("\\[[^\\]]*\\]"),
		inside = grammar(
			"punctuation" to RegExp("[\\[\\],]"),
			"number" to RegExp(NUMBER),
			"string" to quotedString(),
		),
	),
)

/** Registers the `mcfunction` grammar. Token order is matching order, so the most specific ones come first. */
fun initMCFunctionHighlighting() {
	Prism.languages["mcfunction"] = grammar(
		"comment" to token(RegExp("^\\s*#.*$", "m"), greedy = true),
		"command" to token(RegExp("^\\s*([a-z_]+)(?=\\s|$)", "m"), alias = "keyword"),
		"resourceName" to token(RegExp("#?[a-z_][a-z0-9_\\.-]*:[a-z0-9_\\.\\/-]+"), alias = "function"),
		"selector" to token(RegExp("@[apers](?:\\[([^\\]]+)\\])?"), alias = "class-name", inside = selectorArguments),
		"coordinate" to token(RegExp("[~^]|(?:[+-]?\\d*\\.?\\d+)"), alias = "number"),
		"operator" to token(RegExp("[\\-%?!+*<>\\/|&=.:,;]"), alias = "operator"),
		"property" to token(RegExp("#?[a-z_][a-z_\\.-]*:[a-z0-9_\\.-\\/]+(?=\\s*[=:])"), alias = "property"),
		"nbtPath" to token(RegExp("[\\w\\-.]+(?:\\[[^\\]]*\\])*(?:\\.[\\w\\-.]+(?:\\[[^\\]]*\\])*)*"), inside = nbtPathParts),
		"string" to token(RegExp(ESCAPED_STRING), greedy = true),
		"json" to token(RegExp("\\{(?:[^{}]|\\{(?:[^{}]|\\{[^{}]*\\})*\\})*\\}"), inside = jsonBody),
		"nbt" to token(RegExp("\\{[^}]*\\}"), inside = nbtBody),
		"punctuation" to RegExp("[\\[\\](){}:,]"),
	)
}
