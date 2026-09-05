package io.github.ayfri.kore.optimization.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.optimization.PassResult

/** Applies [rewrite] to every line of every function, keeping the line as is when it returns `null`. */
internal fun DataPack.rewriteLines(rewrite: (String) -> String?, summary: (Int) -> String): PassResult {
	var changes = 0

	(functions + generatedFunctions).forEach { function ->
		function.lines.forEachIndexed { index, line ->
			val rewritten = rewrite(line) ?: return@forEachIndexed
			function.lines[index] = rewritten
			changes++
		}
	}

	return if (changes == 0) PassResult.NONE else PassResult(changes, summary(changes))
}
