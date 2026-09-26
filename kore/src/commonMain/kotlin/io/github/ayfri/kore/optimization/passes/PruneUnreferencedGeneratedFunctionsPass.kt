package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.functionsById
import io.github.ayfri.kore.optimization.utils.idsReferencedByResources
import io.github.ayfri.kore.optimization.utils.mentionedIds

/**
 * Removes generated functions whose id is mentioned nowhere else in the pack.
 *
 * Generated functions only exist because a DSL construct created one, so they are unreachable as soon as the line that
 * called them is gone, which happens when a later edit of the same [DataPack] drops the callsite or when an earlier
 * pass removes it. Pruning a function re-checks the generated functions it called, so a chain of generated functions
 * calling each other collapses in one run; two of them calling each other are kept, since each still references the other.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object PruneUnreferencedGeneratedFunctionsPass : DataPackPass {
	override val name = "prune-unreferenced-generated-functions"

	override fun run(dataPack: DataPack): PassResult {
		val fromResources = dataPack.idsReferencedByResources()
		val mentions = dataPack.functionsById()
		val generatedById = dataPack.generatedFunctions.associateBy { it.asId() }
		val pruned = HashSet<Function>()
		val queue = ArrayDeque(dataPack.generatedFunctions)

		while (queue.isNotEmpty()) {
			val function = queue.removeFirst()
			val id = function.asId()
			if (function in pruned || id in fromResources) continue
			if (mentions[id].orEmpty().any { it !== function && it !in pruned }) continue

			pruned += function
			function.lines.forEach { line -> mentionedIds(line).forEach { generatedById[it]?.let(queue::addLast) } }
		}

		if (pruned.isEmpty()) return PassResult.NONE
		dataPack.generatedFunctions.removeAll(pruned)
		return PassResult(pruned.size, "pruned ${pruned.size} unreferenced generated functions")
	}
}
