package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.externals.get
import io.github.ayfri.kore.website.externals.grammar
import io.github.ayfri.kore.website.externals.set
import io.github.ayfri.kore.website.externals.token
import kotlin.js.RegExp

/** Keywords even right before `(` or `{`, where any other name is a call. Same lists as `website/monaco/kotlin-grammar.mjs`. */
private val controlKeywords = listOf(
	"as", "break", "catch", "class", "constructor", "context", "continue", "do", "else", "false", "finally", "for", "fun", "if",
	"in", "init", "interface", "is", "null", "object", "package", "return", "super", "suspend", "this", "throw", "true", "try",
	"typealias", "val", "var", "when", "where", "while",
)

/** Keywords only when no `(` or `{` follows, so `data(...)` and `set(...)` read as the DSL calls they are. */
private val softKeywords = listOf(
	"abstract", "actual", "annotation", "by", "companion", "const", "crossinline", "data", "dynamic", "enum", "expect", "external",
	"final", "get", "import", "infix", "inline", "inner", "internal", "lateinit", "noinline", "open", "operator", "out", "override",
	"private", "protected", "public", "reified", "sealed", "set", "tailrec", "vararg",
)

private val keywords = (controlKeywords + softKeywords).joinToString("|")

/** Keywords that can't be the left operand of an infix call, unlike `this`, `true`, `false` and `null`. */
private val nonOperandKeywords = (controlKeywords + softKeywords - setOf("false", "null", "this", "true")).joinToString("|")

/** Optional type arguments between a name and its parentheses, as in `listOf<String>(`. */
private const val TYPE_ARGUMENTS = """(?:\s*<[\w\s,.?*:<>]*>)?"""

/** Only `get()` and `set(value)` are accessors, `set(self(), ...)` is a call. */
private val keyword = """(^|[^.])\b(?:(?:${controlKeywords.joinToString("|")})\b|get(?=\s*\(\s*\))|set(?=\s*\(\s*value\s*\))|""" +
	"""(?:${softKeywords.joinToString("|")})\b(?!$TYPE_ARGUMENTS\s*[({]))"""

/**
 * An operand, then `name value` on the same line: `name` is an infix call (`"round" greaterThan 1`, `0 until n`) unless it's
 * a keyword (`x in xs`) or an `if` branch (`if (a) b else c`). Greedy, so its lookbehind reads operands already tokenized.
 */
private val infixCall = """((?:"(?:[^"\\\r\n]|\\.)*"|'(?:[^'\\\r\n]|\\.)+'|\b(?!(?:$nonOperandKeywords)\b)\w+|[)\]])[ \t]+)""" +
	"""(?!(?:$keywords)\b)[a-z_]\w*(?=[ \t]+(?!else\b)[\w"'(`!-])"""

/**
 * Aligns Prism's Kotlin grammar with IntelliJ's highlighting, as `website/monaco/kotlin-grammar.mjs` does for the playground
 * editor: calls (`call`) apart from declarations and constructors, `it`, constants, named arguments and string escapes.
 */
fun initKotlinHighlighting() {
	val kotlin = Prism.languages["kotlin"] ?: return
	val tokens = kotlin.asDynamic()
	val escape = RegExp("""\\(?:[bnrt'"\\$]|u[0-9a-fA-F]{4})""")

	// In-place changes come first: `insertBefore` swaps the grammar object, leaving `kotlin` stale.
	kotlin["keyword"] = token(RegExp(keyword), lookbehind = true)
	kotlin["function"] = token(RegExp("""(?:`[^\r\n`]+`|\b[a-z_]\w*)(?=$TYPE_ARGUMENTS\s*[({])"""), greedy = true, alias = "call")
	tokens.annotation.alias = undefined
	tokens.label.alias = undefined
	tokens.char.inside = grammar("escape" to escape)
	val singleLine = tokens["string-literal"][1].inside
	tokens["string-literal"][1].inside = grammar("interpolation" to singleLine.interpolation, "escape" to escape, "string" to singleLine.string)

	// Groups the dotted path so its segments are neither keywords (`import ...utils.set`) nor calls.
	Prism.languages.insertBefore(
		"kotlin", "annotation", grammar(
			"namespace" to token(
				RegExp("""^(\s*)(?:import|package)\s+[\w.*`]+(?:\s+as\s+\w+)?""", "m"),
				lookbehind = true,
				greedy = true,
				inside = grammar(
					"keyword" to RegExp("""\b(?:import|package|as)\b"""),
					"constant" to RegExp("""\b[A-Z][A-Z\d_]+\b"""),
					"class-name" to RegExp("""\b[A-Z]\w*"""),
					"punctuation" to RegExp("[.*]"),
				),
			),
		)
	)

	// Declarations match before `keyword` takes the `fun` or `class` their lookbehind needs.
	Prism.languages.insertBefore(
		"kotlin", "keyword", grammar(
			"class-definition" to token(RegExp("""(\b(?:class|interface|object|typealias)\s+)\w+"""), lookbehind = true, alias = "class-name"),
			"function-definition" to token(
				RegExp("""(\bfun\s+(?:<[\w\s,.?*:<>]*>\s*)?(?:[\w<>?,. ]*\.)?)(?:`[^`\r\n]+`|\w+)(?=\s*\()"""),
				lookbehind = true,
				alias = "function",
			),
			"implicit-parameter" to token(RegExp("""(^|[^.\w])\bit\b(?!\s*[({=])"""), lookbehind = true),
		)
	)

	Prism.languages.insertBefore(
		"kotlin", "function", grammar(
			"constant" to RegExp("""\b[A-Z][A-Z\d_]+\b"""),
			"constructor" to token(RegExp("""\b[A-Z]\w*(?=$TYPE_ARGUMENTS\s*\()"""), greedy = true, alias = "function"),
			"class-name" to token(RegExp("""(?<!["'`])\b[A-Z]\w*\b(?!$TYPE_ARGUMENTS\s*\()"""), greedy = true),
			"named-argument" to token(RegExp("""(?<=[(,]\s*)[a-z_]\w*(?=\s*=(?!=))""")),
			"infix-call" to token(RegExp(infixCall), lookbehind = true, greedy = true, alias = "call"),
		)
	)
}
