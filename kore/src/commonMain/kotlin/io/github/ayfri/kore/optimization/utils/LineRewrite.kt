package io.github.ayfri.kore.optimization.utils

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.functions.generatedFunctionName
import io.github.ayfri.kore.optimization.PassResult

/** Where an inline argument sits in a line: [range] is the text a pass replaces, [key] identifies what it stands for. */
internal class InlineSpan(val key: String, val range: IntRange)

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

/**
 * Replaces the inline arguments found by [locate] that appear in at least [minUses] lines of the pack with the ID of a
 * file made by [create], which receives the span key and a content-based file name (`<generated functions folder>/<prefix>_<hash>`)
 * and returns the ID, or `null` to leave that argument inline. A name already taken by another key is left inline too.
 */
internal fun DataPack.hoistRepeated(
	prefix: String,
	minUses: Int,
	locate: (String) -> List<InlineSpan>,
	create: (key: String, fileName: String) -> String?,
	summary: (files: Int, lines: Int) -> String,
): PassResult {
	val uses = LinkedHashMap<String, Int>()
	(functions + generatedFunctions).forEach { function ->
		function.lines.forEach { line -> locate(line).forEach { uses[it.key] = (uses[it.key] ?: 0) + 1 } }
	}

	val claimed = HashMap<String, String>()
	val ids = HashMap<String, String>()
	uses.forEach { (key, count) ->
		if (count < minUses) return@forEach
		val fileName = "${configuration.generatedFunctionsFolder}/${generatedFunctionName(prefix, listOf(key))}"
		if (claimed.getOrPut(fileName) { key } != key) return@forEach
		create(key, fileName)?.let { ids[key] = it }
	}

	if (ids.isEmpty()) return PassResult.NONE
	return rewriteLines({ line ->
		val spans = locate(line).filter { it.key in ids }
		if (spans.isEmpty()) return@rewriteLines null
		spans.sortedByDescending { it.range.first }.fold(line) { text, span -> text.replaceRange(span.range, ids.getValue(span.key)) }
	}) { summary(ids.size, it) }
}
