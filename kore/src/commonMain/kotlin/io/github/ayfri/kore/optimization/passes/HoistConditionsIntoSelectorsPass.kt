package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.ExecuteChain
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.utils.rewriteLines

private val CONDITION = Regex("""^if score @s (\S+) matches (\S+)$""")
private val EXECUTOR_CHANGERS = setOf("as", "on", "store", "summon")
/** `@p`, `@r` and `@n` carry an implicit `limit=1`, which picks one entity before the hoisted condition would filter. */
private val SELECTOR = Regex("""^as @[aes](\[.*\])?$""")
/** A `limit` applies after the filters, and the game keeps only the last value of an objective repeated in `scores`. */
private val UNMERGEABLE_ARGUMENTS = setOf("limit", "scores")

/**
 * Moves the score conditions testing the executor into the selector that picked it.
 *
 * `execute as @e[type=marker] if score @s timer matches 1 run say hi` becomes
 * `execute as @e[type=marker,scores={timer=1}] run say hi`: one execution context per matching entity instead of one
 * per marker, and one less command node in the chain. The range syntax is the same on both sides, so the rewrite is a
 * move, not a translation.
 *
 * A condition is only hoisted while nothing between the `as` and the condition can change the executor, only into an
 * `@a`, `@e` or `@s` selector carrying neither `scores` nor `limit`, and only once per objective.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object HoistConditionsIntoSelectorsPass : DataPackPass {
	override val name = "hoist-conditions-into-selectors"

	override fun run(dataPack: DataPack) = dataPack.rewriteLines(::hoist) { "hoisted the conditions of $it execute chains" }

	internal fun hoist(line: String): String? {
		val chain = ExecuteChain.parse(line) ?: return null
		val clauses = chain.clauses.toMutableList()
		var hoisted = false

		clauses.indices.forEach { index ->
			val selector = clauses[index].takeIf { SELECTOR.matches(it) }?.removePrefix("as ") ?: return@forEach
			val scores = linkedMapOf<String, String>()
			val removed = mutableListOf<Int>()

			for (next in index + 1 until clauses.size) {
				if (clauses[next].substringBefore(' ') in EXECUTOR_CHANGERS) break
				val (objective, range) = CONDITION.matchEntire(clauses[next])?.destructured ?: continue
				if (objective in scores) continue
				scores[objective] = range
				removed += next
			}

			if (scores.isEmpty()) return@forEach
			val merged = withScores(selector, scores.map { (objective, range) -> "$objective=$range" }) ?: return@forEach
			removed.forEach { clauses[it] = "" }
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
		if (arguments.any { it.substringBefore('=').trim() in UNMERGEABLE_ARGUMENTS }) return null
		return "${selector.substringBefore('[')}[${(arguments + "scores={${scores.joinToString(",")}}").joinToString(",")}]"
	}
}
