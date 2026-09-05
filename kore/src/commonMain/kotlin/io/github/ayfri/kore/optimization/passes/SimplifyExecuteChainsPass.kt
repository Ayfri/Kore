package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.ExecuteChain
import io.github.ayfri.kore.optimization.utils.rewriteLines

/**
 * Rewrites `execute` chains into the shortest form running the exact same command.
 *
 * Three rewrites, all of them behavior-preserving:
 * - a chain with no clause left, `execute run <command>`, unwraps to `<command>`,
 * - an `as @s` clause preceded by another `as` is dropped, since the executor it re-selects is already the current one,
 * - a clause repeated right after itself is dropped when running it twice cannot fork the execution context.
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
			if (clause == kept.lastOrNull() && isForkFree(clause)) return@forEach
			kept += clause
		}

		return chain.copy(clauses = kept).toString().takeIf { it != line.trim() }
	}

	/** A clause is fork-free when re-running it cannot multiply the execution contexts, so a duplicate is a no-op. */
	private fun isForkFree(clause: String) = when (clause.substringBefore(' ')) {
		"align", "anchored", "in" -> true
		"positioned", "rotated" -> clause.split(' ').getOrNull(1) != "as"
		"as", "at" -> clause.endsWith(" @s")
		else -> false
	}
}
