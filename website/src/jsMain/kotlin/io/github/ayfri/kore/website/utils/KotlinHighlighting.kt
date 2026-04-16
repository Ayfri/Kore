package io.github.ayfri.kore.website.utils

fun initKotlinHighlighting() {
	val prism = js("window.Prism")
	val kotlin = prism.languages.kotlin

	// Override Prism's default `function` patterns to also recognize DSL builders (`word {`) and infix
	// calls (`receiver word arg`), which vanilla Prism Kotlin misses.
	// Soft-keywords used as operators (and/or/xor/in/is/as/by/...) are excluded so they stay highlighted as keywords.
	kotlin.function = js(
		"""[
			{ pattern: /(?:`[^\r\n`]+`|\b\w+)(?=\s*\()/, greedy: true },
			{ pattern: /(\.)(?:`[^\r\n`]+`|\w+)(?=\s*\{)/, lookbehind: true, greedy: true },
			{ pattern: /\b[a-z_]\w*(?=\s*\{)/, greedy: true },
			{ pattern: /(?<=(?:\w|\)|\])\s+)(?!(?:and|or|xor|shl|shr|ushr|inv|in|is|as|by|downTo|until|step)\b)(?:`[^\r\n`]+`|[a-z_]\w*)(?=\s+(?:[`"'(\w]|-?\d))/, greedy: true }
		]"""
	)

	// Prism Kotlin deletes `class-name` from the clike base; re-add it so PascalCase references (types, companions) are highlighted.
	prism.languages.insertBefore("kotlin", "function", obj {
		`class-name` = obj {
			pattern = js("/(?<![\"'`])\\b[A-Z][a-zA-Z0-9_]*\\b(?!\\s*[({])/")
			greedy = true
		}
	})

	// Match `Thing.VALUE` as a dedicated enum-value token so the constant part can be styled distinctly (white italic).
	prism.languages.insertBefore("kotlin", "class-name", obj {
		`enum-value` = obj {
			pattern = js("/\\b[A-Z][a-zA-Z0-9_]*\\s*\\.\\s*[A-Z][A-Z0-9_]+\\b/")
			greedy = true
			inside = obj {
				`class-name` = js("/^[A-Z][a-zA-Z0-9_]*/")
				punctuation = js("/\\./")
				constant = js("/[A-Z][A-Z0-9_]+$/")
			}
		}
	})
}
