import java.util.*

fun String.asSerializer() = "${this}Serializer"

fun String.camelCase(separator: String = "_"): String {
	val words = lowercase().split(separator)
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase(Locale.ENGLISH) }
	}
}

fun String.pascalCase(separator: String = "_") = camelCase(separator).replaceFirstChar { it.titlecase(Locale.ENGLISH) }

fun String.snakeCase() = replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()

fun String.isIntOrBoolean() = this == "Integer" || this == "Boolean"

fun String.indentLines(indent: Int = 1) = replace(Regex("^", RegexOption.MULTILINE), "\t".repeat(indent))
