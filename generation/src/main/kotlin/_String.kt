fun String.asSerializer() = "${this}Serializer"

fun String.countOccurrences(substring: String) = split(substring).size - 1

fun String.camelCase(separator: String = "_"): String {
	val words = lowercase().split(separator)
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase() }
	}
}

fun String.pascalCase(separator: String = "_") = camelCase(separator).replaceFirstChar { it.titlecase() }

fun String.snakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
