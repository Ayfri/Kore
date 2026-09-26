package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.idsReferencedByResources
import io.github.ayfri.kore.optimization.utils.namespacedIdToken

/**
 * Merges functions sharing the exact same body, redirecting the calls to the survivor.
 *
 * `DataPack.addGeneratedFunction` already merges two generated functions built with identical lines, but only at the
 * moment they are created: two of them become identical again once an earlier pass rewrites their lines, and nothing
 * ever merged the user functions. A duplicate is only dropped when no resource mentions its id, since a function tag
 * or an advancement reward holds the id in a typed field the pass cannot rewrite.
 *
 * @property includeUserFunctions whether hand-written functions take part, off by default since their names are part of
 * what the pack exposes and something outside the pack may call them
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data class DedupeFunctionsPass(val includeUserFunctions: Boolean = false) : DataPackPass {
	override val name = "dedupe-functions"

	override fun run(dataPack: DataPack): PassResult {
		val fromResources = dataPack.idsReferencedByResources()
		val userFunctions = dataPack.functions.toHashSet()
		val duplicates = (dataPack.functions + dataPack.generatedFunctions)
			.groupBy { it.commandLines }
			.filterKeys(List<String>::isNotEmpty)
			.values
			.filter { it.size > 1 }

		val redirects = HashMap<String, String>()
		val merged = HashSet<Function>()
		duplicates.forEach { group ->
			val keeper = group.firstOrNull { it in userFunctions } ?: group.first()

			group.filter { it !== keeper && (includeUserFunctions || it !in userFunctions) && it.asId() !in fromResources }.forEach {
				redirects[it.asId()] = keeper.asId()
				merged += it
			}
		}

		if (merged.isEmpty()) return PassResult.NONE
		dataPack.functions.removeAll(merged)
		dataPack.generatedFunctions.removeAll(merged)
		(dataPack.functions + dataPack.generatedFunctions).forEach { function ->
			function.lines.forEachIndexed { index, line ->
				function.lines[index] = namespacedIdToken.replace(line) { redirects[it.value] ?: it.value }
			}
		}

		return PassResult(merged.size, "merged ${merged.size} duplicated functions")
	}
}
