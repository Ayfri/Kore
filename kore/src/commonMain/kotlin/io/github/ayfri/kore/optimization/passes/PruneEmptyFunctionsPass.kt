package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.idsReferencedByResources
import io.github.ayfri.kore.optimization.utils.mentionedIds

/**
 * `store` writes a result even when the call does nothing, `summon`/`on` change the executor, `return` exits the caller,
 * `if function` runs another function.
 */
private val UNSAFE_CLAUSES = listOf("store ", "summon ", " on ", "return ", " if function ", " unless function ")
private val CALL_PATTERN = Regex("""^(?:execute\s+.*\brun\s+)?function\s+(\S+)(?:\s.*)?$""")

/**
 * Removes user functions containing no command, and the calls made to them.
 *
 * A function is kept when any resource still mentions its id (a function tag, an advancement reward, an item
 * modifier...), or when a line other than a removable call mentions it (`schedule function`, `return run function`,
 * a macro line...), since dropping it would break that reference. Pruning runs until nothing changes, so a function
 * left empty by the removal of its own calls is pruned in turn.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object PruneEmptyFunctionsPass : DataPackPass {
	override val name = "prune-empty-functions"

	override fun run(dataPack: DataPack): PassResult {
		val fromResources = dataPack.idsReferencedByResources()
		var prunedFunctions = 0
		var removedCalls = 0

		while (true) {
			val prunedIds = dataPack.functions.filter { it.commandLines.isEmpty() && it.asId() !in fromResources }.mapTo(HashSet(), Function::asId)
			(dataPack.functions + dataPack.generatedFunctions).forEach { function ->
				function.lines.forEach { line ->
					if (!line.trimStart().startsWith('#') && !isRemovableCall(line, prunedIds)) prunedIds -= mentionedIds(line).toSet()
				}
			}

			val pruned = dataPack.functions.filter { it.asId() in prunedIds }
			if (pruned.isEmpty()) break

			dataPack.functions -= pruned.toSet()
			prunedFunctions += pruned.size

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

	private fun isRemovableCall(line: String, prunedIds: Set<String>): Boolean {
		val trimmed = line.trim()
		return !(trimmed.startsWith('$') || UNSAFE_CLAUSES.any { it in trimmed }) && CALL_PATTERN.matchEntire(trimmed)?.groupValues?.get(1) in prunedIds
	}
}
