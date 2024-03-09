package io.github.ayfri.kore.utils

import java.util.*

fun String.ifNotEmpty(block: (String) -> String) = if (isNotEmpty()) block(this) else this

fun String.pascalCase() = split("_").joinToString("") { word ->
	word.replaceFirstChar {
		if (it.isLowerCase()) it.titlecase(Locale.getDefault())
		else it.toString()
	}
}

fun String.snakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()

/**
 * Unescape a string.
 * Traverse the string and replace escaped characters with their unescaped version.
 * If the string was inside a double quote, the returned string will be without the quotes.
 * @receiver The string to unescape.
 * @return The unescaped string.
 */
fun String.unescape(): String {
	var result = this
	(0..<result.length - 2)
		.asSequence()
		.filter { it in result.indices && result[it] == '\\' }
		.forEach { result = result.replaceRange(it, it + 2, result[it + 1].toString()) }

	return when {
		result.startsWith('"') && result.endsWith('"') -> result.substring(1, result.length - 1)
		else -> result
	}
}
