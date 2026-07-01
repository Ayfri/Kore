package io.github.ayfri.kore.bindings.generation


fun String.camelCase(vararg separators: String = arrayOf("_")): String {
	val words = lowercase().split(*separators)
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase() }
	}
}

fun String.pascalCase(vararg separators: String = arrayOf("_")) =
	camelCase(*separators).replaceFirstChar { it.titlecase() }

fun String.snakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
