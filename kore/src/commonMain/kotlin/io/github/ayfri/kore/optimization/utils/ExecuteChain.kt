package io.github.ayfri.kore.optimization.utils

/**
 * Minimal `execute` chain parser shared by the optimization passes, splitting a line into its clauses and its final command.
 *
 * It is deliberately partial: any shape it does not fully understand yields `null`, so a pass leaves the line untouched
 * rather than guessing. Chains carrying a quoted string in their clauses are refused too, since clause splitting works
 * on whitespace. A `run execute` is flattened into one chain, `execute as @a run execute at @s run say hi` parsing as
 * `execute as @a at @s run say hi`, which the game runs identically.
 */
internal data class ExecuteChain(val clauses: List<String>, val command: String) {
	override fun toString() = if (clauses.isEmpty()) command else "execute ${clauses.joinToString(" ")} run $command"

	companion object {
		private val pattern = Regex("""^execute\s+(.*?)\s*\brun\s+(.+)$""")
		val keywords = setOf("align", "anchored", "as", "at", "facing", "if", "in", "on", "positioned", "rotated", "store", "summon", "unless")

		fun parse(line: String): ExecuteChain? {
			val trimmed = line.trim()
			if (trimmed.startsWith('$')) return null

			val (rawClauses, command) = pattern.matchEntire(trimmed)?.destructured ?: return null
			if ('"' in rawClauses || '\'' in rawClauses) return null

			val clauses = splitClauses(rawClauses.split(' ').filter(String::isNotEmpty)) ?: return null
			val inner = if (command.startsWith("execute ")) parse(command) else null
			return if (inner == null) ExecuteChain(clauses, command) else ExecuteChain(clauses + inner.clauses, inner.command)
		}

		/** Splits clause tokens using each subcommand's arity, `if`/`unless`/`store` running up to the next subcommand keyword. */
		private fun splitClauses(tokens: List<String>): List<String>? {
			val clauses = mutableListOf<String>()
			var index = 0

			while (index < tokens.size) {
				val arity = when (tokens[index]) {
					"align", "anchored", "as", "at", "in", "on", "summon" -> 1
					"facing" -> 3
					"positioned" -> if (tokens.getOrNull(index + 1) in setOf("as", "over")) 2 else 3
					"rotated" -> 2
					"if", "store", "unless" -> null
					else -> return null
				}

				val end = arity?.let { index + 1 + it } ?: tokens.subList(index + 1, tokens.size).indexOfFirst { it in keywords }.let {
					if (it == -1) tokens.size else index + 1 + it
				}

				if (end > tokens.size) return null
				val clause = tokens.subList(index, end)
				/** An objective named like a keyword (`if score @s run matches 1 run ...`) cuts a score clause short of its 5 or 6 tokens. */
				if (clause.getOrNull(1) == "score" && clause.size != 5 && clause.size != 6) return null
				clauses += clause.joinToString(" ")
				index = end
			}

			return clauses
		}
	}
}
