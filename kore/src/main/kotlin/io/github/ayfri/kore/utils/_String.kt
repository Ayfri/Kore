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
