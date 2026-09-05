package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.optimization.utils.functionIdPattern
import io.github.ayfri.kore.optimization.utils.generatorsJson

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
		val json = dataPack.generatorsJson()
		val duplicates = (dataPack.functions + dataPack.generatedFunctions)
			.groupBy { it.commandLines }
			.filterKeys(List<String>::isNotEmpty)
			.values
			.filter { it.size > 1 }

		var merged = 0
		duplicates.forEach { group ->
			val keeper = group.firstOrNull { it in dataPack.functions } ?: group.first()

			group.filter { it !== keeper && it.canBeMerged(dataPack, json) }.forEach { duplicate ->
				dataPack.functions -= duplicate
				dataPack.generatedFunctions -= duplicate
				dataPack.replaceCalls(duplicate, keeper)
				merged++
			}
		}

		if (merged == 0) return PassResult.NONE
		return PassResult(merged, "merged $merged duplicated functions")
	}

	private fun Function.canBeMerged(dataPack: DataPack, json: String) =
		(includeUserFunctions || this !in dataPack.functions) && asId() !in json

	private fun DataPack.replaceCalls(duplicate: Function, keeper: Function) {
		val pattern = functionIdPattern(duplicate.asId())
		val keeperId = keeper.asId()

		(functions + generatedFunctions).forEach { function ->
			function.lines.forEachIndexed { index, line ->
				if (pattern.containsMatchIn(line)) function.lines[index] = pattern.replace(line, keeperId)
			}
		}
	}
}
