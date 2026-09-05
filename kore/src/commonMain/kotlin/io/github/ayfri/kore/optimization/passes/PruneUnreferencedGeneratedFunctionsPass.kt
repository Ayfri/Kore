package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.generatorsJson
import io.github.ayfri.kore.optimization.utils.isCalled

/**
 * Removes generated functions whose id is mentioned nowhere else in the pack.
 *
 * Generated functions only exist because a DSL construct created one, so they are unreachable as soon as the line that
 * called them is gone, which happens when a later edit of the same [DataPack] drops the callsite or when an earlier
 * pass removes it. Pruning repeats until it converges, so a chain of generated functions calling each other collapses
 * in one run; two of them calling each other are kept, since each still references the other.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object PruneUnreferencedGeneratedFunctionsPass : DataPackPass {
	override val name = "prune-unreferenced-generated-functions"

	override fun run(dataPack: DataPack): PassResult {
		var pruned = 0

		while (true) {
			if (dataPack.generatedFunctions.isEmpty()) break

			val json = dataPack.generatorsJson()
			val unreferenced = dataPack.generatedFunctions.filter { it.asId() !in json && !dataPack.isCalled(it) }

			if (unreferenced.isEmpty()) break
			dataPack.generatedFunctions -= unreferenced.toSet()
			pruned += unreferenced.size
		}

		if (pruned == 0) return PassResult.NONE
		return PassResult(pruned, "pruned $pruned unreferenced generated functions")
	}
}
