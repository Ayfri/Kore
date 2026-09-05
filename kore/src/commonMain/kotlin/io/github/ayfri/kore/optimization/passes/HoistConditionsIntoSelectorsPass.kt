package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.ExecuteChain
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.utils.rewriteLines

/**
 * Moves the score conditions testing the executor into the selector that picked it.
 *
 * `execute as @e[type=marker] if score @s timer matches 1 run say hi` becomes
 * `execute as @e[type=marker,scores={timer=1}] run say hi`: one execution context per matching entity instead of one
 * per marker, and one less command node in the chain. The range syntax is the same on both sides, so the rewrite is a
 * move, not a translation.
 *
 * A condition is only hoisted while nothing between the `as` and the condition can change the executor, and only into
 * a selector that carries no `scores` argument yet.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object HoistConditionsIntoSelectorsPass : DataPackPass {
	private val condition = Regex("""^if score @s (\S+) matches (\S+)$""")
	private val executorChangers = setOf("as", "on", "store", "summon")

	override val name = "hoist-conditions-into-selectors"

	override fun run(dataPack: DataPack) = dataPack.rewriteLines(::hoist) { "hoisted the conditions of $it execute chains" }

	internal fun hoist(line: String): String? {
		val chain = ExecuteChain.parse(line) ?: return null
		val clauses = chain.clauses.toMutableList()
		var hoisted = false

		clauses.indices.forEach { index ->
			val selector = clauses[index].takeIf { it.startsWith("as @") }?.removePrefix("as ") ?: return@forEach
			val scores = mutableListOf<String>()

			for (next in index + 1 until clauses.size) {
				if (clauses[next].substringBefore(' ') in executorChangers) break
				val (objective, range) = condition.matchEntire(clauses[next])?.destructured ?: continue
				scores += "$objective=$range"
				clauses[next] = ""
			}

			if (scores.isEmpty()) return@forEach
			val merged = withScores(selector, scores) ?: return@forEach
			clauses[index] = "as $merged"
			hoisted = true
		}

		if (!hoisted) return null
		return chain.copy(clauses = clauses.filter(String::isNotEmpty)).toString()
	}

	/** Adds a `scores` argument to a selector, or returns `null` when it already has one and merging would be ambiguous. */
	private fun withScores(selector: String, scores: List<String>): String? {
		val body = selector.substringAfter('[', "").removeSuffix("]")
		if (body.isEmpty()) return "${selector}[scores={${scores.joinToString(",")}}]"

		val arguments = Selectors.splitArguments(body) ?: return null
		if (arguments.any { it.substringBefore('=').trim() == "scores" }) return null
		return "${selector.substringBefore('[')}[${(arguments + "scores={${scores.joinToString(",")}}").joinToString(",")}]"
	}
}
