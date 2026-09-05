package io.github.ayfri.kore.optimization.utils

/** Bracketed-selector scanner shared by the passes rewriting selector arguments, aware of nesting and of quoted strings. */
internal object Selectors {
	private val header = Regex("""@[aenprs]\[""")

	/**
	 * Rewrites every bracketed selector of [text] through [transform], which receives the top-level arguments and
	 * returns the new ones, or `null` to leave that selector alone. Returns `null` when nothing changed.
	 */
	fun rewrite(text: String, transform: (List<String>) -> List<String>?): String? {
		val result = StringBuilder()
		var index = 0
		var changed = false

		while (true) {
			val match = header.find(text, index) ?: break
			val open = match.range.last
			val close = matchingBracket(text, open) ?: break
			val rewritten = splitArguments(text.substring(open + 1, close))?.let(transform)

			result.append(text, index, open + 1)
			result.append(rewritten?.joinToString(",")?.also { changed = true } ?: text.substring(open + 1, close))
			result.append(']')
			index = close + 1
		}

		if (!changed) return null
		return result.append(text, index, text.length).toString()
	}

	/** Splits the body of a selector on its top-level commas, ignoring the ones nested in a list, a compound or a string. */
	fun splitArguments(body: String): List<String>? {
		if (body.isBlank()) return emptyList()
		val arguments = mutableListOf<String>()
		var depth = 0
		var start = 0
		var quote: Char? = null
		var escaped = false

		body.forEachIndexed { index, char ->
			when {
				escaped -> escaped = false
				char == '\\' && quote != null -> escaped = true
				quote != null -> if (char == quote) quote = null
				char == '"' || char == '\'' -> quote = char
				char == '[' || char == '{' -> depth++
				char == ']' || char == '}' -> depth--
				char == ',' && depth == 0 -> {
					arguments += body.substring(start, index)
					start = index + 1
				}
			}

			if (depth < 0) return null
		}

		if (depth != 0 || quote != null) return null
		arguments += body.substring(start)
		return if (arguments.any(String::isBlank)) null else arguments
	}

	/** Index of the `]` closing the bracket opened at [open], or `null` when the selector is unbalanced. */
	private fun matchingBracket(text: String, open: Int): Int? {
		var depth = 0
		var quote: Char? = null
		var escaped = false

		for (index in open until text.length) {
			val char = text[index]
			when {
				escaped -> escaped = false
				char == '\\' && quote != null -> escaped = true
				quote != null -> if (char == quote) quote = null
				char == '"' || char == '\'' -> quote = char
				char == '[' || char == '{' -> depth++
				char == ']' || char == '}' -> {
					depth--
					if (depth == 0) return if (char == ']') index else null
				}
			}
		}

		return null
	}
}
