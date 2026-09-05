package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.generatorsJson

/**
 * Removes user functions containing no command, and the calls made to them.
 *
 * A function is kept when any resource still mentions its id (a function tag, an advancement reward, an item
 * modifier...), since dropping it would break that reference. Pruning runs until nothing changes, so a function
 * left empty by the removal of its own calls is pruned in turn.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object PruneEmptyFunctionsPass : DataPackPass {
	/** `store` writes a result even when the call does nothing, `summon`/`on` change the executor, `return` exits the caller. */
	private val unsafeClauses = listOf("store ", "summon ", " on ", "return ")
	private val callPattern = Regex("""^(?:execute\s+.*\brun\s+)?function\s+(\S+)(?:\s.*)?$""")

	override val name = "prune-empty-functions"

	override fun run(dataPack: DataPack): PassResult {
		var prunedFunctions = 0
		var removedCalls = 0

		while (true) {
			val candidates = dataPack.functions.filter { it.commandLines.isEmpty() }
			if (candidates.isEmpty()) break

			val referenced = referencedIds(dataPack, candidates)
			val pruned = candidates.filter { it.asId() !in referenced }
			if (pruned.isEmpty()) break

			dataPack.functions -= pruned.toSet()
			prunedFunctions += pruned.size

			val prunedIds = pruned.mapTo(mutableSetOf(), Function::asId)
			(dataPack.functions + dataPack.generatedFunctions).forEach { function ->
				val kept = function.lines.filterNot { isRemovableCall(it, prunedIds) }
				if (kept.size == function.lines.size) return@forEach

				removedCalls += function.lines.size - kept.size
				function.lines.clear()
				function.lines += kept
			}
		}

		if (prunedFunctions == 0) return PassResult.NONE
		return PassResult(prunedFunctions + removedCalls, "pruned $prunedFunctions empty functions and $removedCalls calls to them")
	}

	/** Serializes every generator once and keeps the ids still appearing in it, the only reliable way to spot non-tag references. */
	private fun referencedIds(dataPack: DataPack, candidates: List<Function>): Set<String> {
		val json = dataPack.generators.flatten().joinToString("\n") { it.generateJson(dataPack) }
		return candidates.mapNotNull { it.asId().takeIf(json::contains) }.toSet()
	}

	private fun isRemovableCall(line: String, prunedIds: Set<String>): Boolean {
		val trimmed = line.trim()
		if (trimmed.startsWith('$') || unsafeClauses.any { it in trimmed }) return false
		return callPattern.matchEntire(trimmed)?.groupValues?.get(1) in prunedIds
	}
}
