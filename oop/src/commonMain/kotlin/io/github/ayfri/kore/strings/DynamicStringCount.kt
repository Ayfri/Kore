package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros

/** Default score holder storing the result of a count operation. */
const val COUNT_RESULT_HOLDER = "#${INTERNAL_NAME_PREFIX}count"

/** Empty holder for the count controller (state is driven by scores). */
class CountMacros internal constructor() : Macros()

/**
 * Recursive count controller. Runs [findControllerHelper] starting from `#kore_string_count_start`,
 * increments `#kore_string_count` on hits, advances past the match and recurses until no more
 * occurrences are found inside the current window.
 */
internal fun DynamicStringRuntime.countControllerHelper(): FunctionWithMacros<CountMacros> =
	ensure(OopConstants.stringCountMacroName, ::CountMacros) {
		findControllerHelper()
		val obj = config.lengthObjective
		val findI = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_i", obj)
		val findBound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", obj)
		val findResult = ScoreCursor(FIND_RESULT_HOLDER, obj)
		val findSubLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
		val countStart = ScoreCursor("#${INTERNAL_NAME_PREFIX}count_start", obj)
		val count = ScoreCursor(COUNT_RESULT_HOLDER, obj)

		scoreOperation(findI, Operation.SET, countStart)
		findResult.set(this, -1)
		ifScoreCompareRunFunction(findI, Relation.LESS_THAN_OR_EQUAL_TO, findBound, OopConstants.stringFindMacroName)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. run " +
				"scoreboard players add ${count.holder} $obj 1"
		)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. run " +
				"scoreboard players operation ${countStart.holder} $obj = ${findResult.holder} $obj"
		)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. run " +
				"scoreboard players operation ${countStart.holder} $obj += ${findSubLen.holder} $obj"
		)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. " +
				"if score ${countStart.holder} $obj <= ${findBound.holder} $obj run " +
				"function ${datapack.name}:${OopConstants.stringCountMacroName}"
		)
	}

private fun primeCountLoop(
	fn: Function,
	source: DynamicString,
	needlePath: String,
	needleLenSetup: (Function) -> Unit,
): ScoreCursor {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val obj = rt.config.lengthObjective
	val controller = rt.countControllerHelper()

	val srcLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_srclen", obj)
	val findSubLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
	val findBound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", obj)
	val countStart = ScoreCursor("#${INTERNAL_NAME_PREFIX}count_start", obj)
	val count = ScoreCursor(COUNT_RESULT_HOLDER, obj)

	context(fn) { source.length(srcLen.holder) }
	needleLenSetup(fn)

	val findArgs = rt.argsPath(OopConstants.stringFindStepMacroName)
	fn.data(rt.libStorageArg) {
		modify("$findArgs.src", source.name)
		modify("$findArgs.needlePath", needlePath)
	}
	findBound.assignFrom(fn, srcLen)
	findBound.subFrom(fn, findSubLen)
	count.set(fn, 0)
	countStart.set(fn, 0)
	fn.ifScoreMatchesRunFunction(findBound, rangeOrIntStart(0), controller.name)
	return count
}

/**
 * Counts the number of occurrences of the literal [needle] in this string, into [resultHolder] on
 * the `kore_string_len` objective.
 *
 * ```
 * "a,b,,c".count(",")  // 3
 * ```
 */
context(fn: Function)
fun DynamicString.count(needle: String, resultHolder: String = COUNT_RESULT_HOLDER): DynamicStringResult {
	require(needle.isNotEmpty()) { "count needle must not be empty." }
	val rt = fn.datapack.requireDynamicStringRuntime()
	val needleScratch = rt.tmpPath("${INTERNAL_NAME_PREFIX}count_needle")
	fn.data(rt.libStorageArg) { modify(needleScratch, needle) }
	val result = primeCountLoop(fn, this, needleScratch) { f ->
		ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", rt.config.lengthObjective).set(f, needle.length)
	}
	if (resultHolder != result.holder) ScoreCursor(resultHolder, result.objective).assignFrom(fn, result)
	return DynamicStringResult(resultHolder, result.objective)
}

/**
 * Dynamic variant of [count] where the needle itself is another [DynamicString].
 *
 * ```
 * // needle holds "," at runtime
 * "a,b,,c".count(needle)  // 3
 * ```
 */
context(fn: Function)
fun DynamicString.count(needle: DynamicString, resultHolder: String = COUNT_RESULT_HOLDER): DynamicStringResult {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val result = primeCountLoop(fn, this, needle.nbtPath) { f ->
		ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", rt.config.lengthObjective)
			.storeValueOfNbt(f, rt.libStorageArg, needle.nbtPath)
	}
	if (resultHolder != result.holder) ScoreCursor(resultHolder, result.objective).assignFrom(fn, result)
	return DynamicStringResult(resultHolder, result.objective)
}
