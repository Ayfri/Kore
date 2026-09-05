package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.externals.get
import io.github.ayfri.kore.website.externals.grammar
import io.github.ayfri.kore.website.externals.set
import io.github.ayfri.kore.website.externals.token
import kotlin.js.RegExp

/**
 * Replaces Prism's default `function` patterns so DSL builders (`word {`) and infix calls (`receiver word arg`) are
 * recognized too, which vanilla Prism Kotlin misses. Soft-keywords used as operators (and/or/xor/in/is/as/by/...) are
 * excluded so they stay highlighted as keywords.
 */
private val functionPatterns
	get() = arrayOf(
		token(RegExp("(?:`[^\\r\\n`]+`|\\b\\w+)(?=\\s*\\()"), greedy = true),
		token(RegExp("(\\.)(?:`[^\\r\\n`]+`|\\w+)(?=\\s*\\{)"), lookbehind = true, greedy = true),
		token(RegExp("\\b[a-z_]\\w*(?=\\s*\\{)"), greedy = true),
		token(
			RegExp(
				"(?<=(?:\\w|\\)|\\])\\s+)(?!(?:and|or|xor|shl|shr|ushr|inv|in|is|as|by|downTo|until|step)\\b)" +
					"(?:`[^\\r\\n`]+`|[a-z_]\\w*)(?=\\s+(?:[`\"'(\\w]|-?\\d))"
			),
			greedy = true,
		),
	)

fun initKotlinHighlighting() {
	Prism.languages["kotlin"]?.set("function", functionPatterns)

	// Prism Kotlin deletes `class-name` from the clike base; re-add it so PascalCase references (types, companions) are highlighted.
	Prism.languages.insertBefore(
		"kotlin", "function", grammar(
			"class-name" to token(RegExp("(?<![\"'`])\\b[A-Z][a-zA-Z0-9_]*\\b(?!\\s*[({])"), greedy = true),
		)
	)

	// Match `Thing.VALUE` as a dedicated enum-value token so the constant part can be styled distinctly (white italic).
	Prism.languages.insertBefore(
		"kotlin", "class-name", grammar(
			"enum-value" to token(
				RegExp("\\b[A-Z][a-zA-Z0-9_]*\\s*\\.\\s*[A-Z][A-Z0-9_]+\\b"),
				greedy = true,
				inside = grammar(
					"class-name" to RegExp("^[A-Z][a-zA-Z0-9_]*"),
					"punctuation" to RegExp("\\."),
					"constant" to RegExp("[A-Z][A-Z0-9_]+$"),
				),
			),
		)
	)
}
