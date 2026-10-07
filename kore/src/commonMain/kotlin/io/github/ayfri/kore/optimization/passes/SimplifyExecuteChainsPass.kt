package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.utils.ExecuteChain
import io.github.ayfri.kore.optimization.utils.Selectors
import io.github.ayfri.kore.optimization.utils.rewriteLines

private val DATA_REMOVE = Regex("""^data remove (.+)$""")
private val EXISTENCE_TEST = Regex("""^(if|unless) entity (@[ae](?:\[.*\])?)$""")
private val ORDERING_ARGUMENTS = setOf("limit", "sort")

/**
 * Rewrites `execute` chains into the shortest form running the exact same command.
 *
 * The rewrites, all of them behavior-preserving:
 * - a chain with no clause left, `execute run <command>`, unwraps to `<command>`, and `run execute` flattens into one chain,
 * - an `as @s` clause preceded by another `as` is dropped, since the executor it re-selects is already the current one,
 * - a clause repeated right after itself is dropped when running it twice changes nothing,
 * - `if entity @e[...]` and `unless entity @e[...]` only test existence, so they lose their `sort` and get `limit=1`,
 *   which lets the game stop at the first match,
 * - `if data <target> <path>` right before `data remove <target> <path>` is dropped, the removal failing on its own when
 *   the path is missing.
 *
 * A leading `as @s` is kept on purpose: a function called from a function tag has no executor, so the chain must still
 * fail there.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object SimplifyExecuteChainsPass : DataPackPass {
	override val name = "simplify-execute-chains"

	override fun run(dataPack: DataPack) = dataPack.rewriteLines(::simplify) { "simplified $it execute chains" }

	/** Returns the shortened line, or `null` when the line is not an `execute` chain or is already minimal. */
	internal fun simplify(line: String): String? {
		val chain = ExecuteChain.parse(line) ?: return null
		val kept = mutableListOf<String>()

		chain.clauses.forEach { clause ->
			if (clause == "as @s" && kept.any { it.startsWith("as ") }) return@forEach
			if (clause == kept.lastOrNull() && isIdempotent(clause)) return@forEach
			kept += existenceTest(clause) ?: clause
		}

		val removedPath = DATA_REMOVE.matchEntire(chain.command)?.groupValues?.get(1)
		if (kept.lastOrNull() == "if data $removedPath" && kept.none { it.startsWith("store ") }) kept.removeLast()

		return chain.copy(clauses = kept).toString().takeIf { it != line.trim() }
	}

	/** A clause is idempotent when re-running it neither multiplies the execution contexts nor moves them further. */
	private fun isIdempotent(clause: String) = when (clause.substringBefore(' ')) {
		"align", "anchored", "in" -> true
		"positioned", "rotated" -> clause.split(' ').getOrNull(1) != "as" && '~' !in clause && '^' !in clause
		"as", "at" -> clause.endsWith(" @s")
		else -> false
	}

	/** `if entity @e[sort=nearest,limit=3]` becomes `if entity @e[limit=1]`, `null` when the clause is not such a test. */
	private fun existenceTest(clause: String): String? {
		val (keyword, selector) = EXISTENCE_TEST.matchEntire(clause)?.destructured ?: return null
		if ('[' !in selector) return "$keyword entity $selector[limit=1]"
		val rewritten = Selectors.rewrite(selector) { arguments ->
			val filters = arguments.filter { it.substringBefore('=').trim() !in ORDERING_ARGUMENTS }
			(filters + "limit=1").takeIf { it != arguments }
		} ?: return null
		return "$keyword entity $rewritten"
	}
}
