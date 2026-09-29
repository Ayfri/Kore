package io.github.ayfri.kore.utils

fun String.camelCase(separator: String = "_"): String {
	val words = lowercase().split(separator)
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase() }
	}
}

fun String.ifNotEmpty(block: (String) -> String) = if (isNotEmpty()) block(this) else this

fun String.pascalCase() = split("_").joinToString("") { word ->
	word.replaceFirstChar {
		if (it.isLowerCase()) it.titlecase()
		else it.toString()
	}
}

// Hoisted: `snakeCase` runs on every polymorphic serial name, so recompiling these per call is pure overhead.
private val ACRONYM_BOUNDARY = Regex("([A-Z]+)([A-Z][a-z])")
private val LETTER_THEN_DIGIT = Regex("([a-z])([0-9])")
private val LOWER_THEN_UPPER = Regex("([a-z])([A-Z])")

fun String.snakeCase() = replace(ACRONYM_BOUNDARY, "$1_$2")
	.replace(LETTER_THEN_DIGIT, "$1_$2")
	.replace(LOWER_THEN_UPPER, "$1_$2")
	.lowercase()
