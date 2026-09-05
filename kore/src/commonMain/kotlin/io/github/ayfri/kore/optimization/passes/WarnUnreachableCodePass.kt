package io.github.ayfri.kore.optimization.passes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.DataPackPass
import io.github.ayfri.kore.optimization.PassResult
import io.github.ayfri.kore.utils.warn

/**
 * Reports the commands sitting after an unconditional `return` in the same function, which the game never runs.
 *
 * Only a top-level `return` ends the function for sure, so a `return` behind an `execute` chain or on a macro line is
 * ignored. Nothing is removed, since dead code after a `return` is almost always a mistake in the surrounding logic
 * rather than something to silently drop.
 *
 * Docs: https://kore.ayfri.com/docs/guides/optimization
 */
data object WarnUnreachableCodePass : DataPackPass {
	override val name = "warn-unreachable-code"

	override fun run(dataPack: DataPack): PassResult {
		var unreachable = 0

		(dataPack.functions + dataPack.generatedFunctions).forEach { function ->
			val returnIndex = function.lines.indexOfFirst { it.trim().let { line -> line == "return" || line.startsWith("return ") } }
			if (returnIndex == -1) return@forEach

			val dead = function.lines.drop(returnIndex + 1).filter { it.isNotBlank() && !it.trimStart().startsWith('#') }
			if (dead.isEmpty()) return@forEach

			unreachable += dead.size
			warn("${function.asId()}: ${dead.size} unreachable line(s) after `${function.lines[returnIndex].trim()}`, first one is `${dead.first().trim()}`.")
		}

		if (unreachable == 0) return PassResult.NONE
		return PassResult(unreachable, "found $unreachable unreachable lines")
	}
}
