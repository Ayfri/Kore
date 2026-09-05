package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

/** Macros for the replace step: takes the source name so sub-macros can build proper paths. */
class ReplaceStepMacros internal constructor() : Macros() {
	val srcName by "srcName"
}

/** Macros holder used by the replace controller function. */
class ReplaceMacros internal constructor() : Macros() {
	val srcName by "srcName"
}

private const val REPLACE_AFTER = "${INTERNAL_NAME_PREFIX}replace_after"
private const val REPLACE_BEFORE = "${INTERNAL_NAME_PREFIX}replace_before"
private const val REPLACE_NEEDLE_KEY = "${INTERNAL_NAME_PREFIX}replace_needle"
private const val REPLACE_NEW_KEY = "${INTERNAL_NAME_PREFIX}replace_new"

/**
 * Macro step performing a single in-place replacement at the already-resolved position
 * `#kore_string_find`:
 *   1. `before = src[0 .. #find)`
 *   2. `after  = src[#find + oldLen .. srcLen)`
 *   3. `src    = before`, then append the replacement, then append `after`.
 */
internal fun DynamicStringRuntime.replaceStepHelper(): FunctionWithMacros<ReplaceStepMacros> =
	ensure(OopConstants.stringReplaceStepMacroName, ::ReplaceStepMacros) {
		substringHelper()
		val obj = config.lengthObjective
		val subArgs = argsPath(OopConstants.stringSubstringMacroName)
		val newPath = tmpPath(REPLACE_NEW_KEY)
		val zero = ScoreCursor("#${INTERNAL_NAME_PREFIX}zero", obj)
		val findResult = ScoreCursor(FIND_RESULT_HOLDER, obj)
		val findSubLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
		val srcLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_srclen", obj)
		val afterStart = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_after_start", obj)

		zero.set(this, 0)
		storeScoreToNbt(zero, libStorageArg, "$subArgs.start")
		storeScoreToNbt(findResult, libStorageArg, "$subArgs.end")
		setNbtString(libStorageArg, "$subArgs.src", macros.srcName)
		setNbtString(libStorageArg, "$subArgs.dst", REPLACE_BEFORE)
		callMacro(OopConstants.stringSubstringMacroName, libStorageArg, subArgs)

		scoreOperation(afterStart, Operation.SET, findResult)
		scoreOperation(afterStart, Operation.ADD, findSubLen)
		storeScoreToNbt(afterStart, libStorageArg, "$subArgs.start")
		storeScoreToNbt(srcLen, libStorageArg, "$subArgs.end")
		setNbtString(libStorageArg, "$subArgs.src", macros.srcName)
		setNbtString(libStorageArg, "$subArgs.dst", REPLACE_AFTER)
		callMacro(OopConstants.stringSubstringMacroName, libStorageArg, subArgs)

		copyNbt(libStorageArg, heapPath(macros.srcName), libStorageArg, heapPath(REPLACE_BEFORE))
		data(libStorageArg) {
			modify(heapPath(macros.srcName)) {
				append(libStorageArg, newPath, null, null)
			}
			modify(heapPath(macros.srcName)) {
				append(libStorageArg, heapPath(REPLACE_AFTER), null, null)
			}
		}
	}

/**
 * Replace controller loop: searches from `#kore_string_replace_start` onwards, mutates the source
 * with the replace-step macro, then moves the cursor past the inserted replacement before
 * re-entering the loop.
 *
 * Restarting the search past the replacement is what keeps `replace("a", "aa")` finite: a naive
 * loop searching from index `0` would keep matching the text it just wrote.
 */
internal fun DynamicStringRuntime.replaceControllerHelper(): FunctionWithMacros<ReplaceMacros> =
	ensure(OopConstants.stringReplaceMacroName, ::ReplaceMacros) {
		findControllerHelper()
		replaceStepHelper()
		val obj = config.lengthObjective
		val stepArgs = argsPath(OopConstants.stringReplaceStepMacroName)
		val controllerArgs = argsPath(OopConstants.stringReplaceMacroName)
		val cap = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_cap", obj)
		val findBound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", obj)
		val findI = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_i", obj)
		val findResult = ScoreCursor(FIND_RESULT_HOLDER, obj)
		val findSubLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
		val newLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_newlen", obj)
		val srcLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_srclen", obj)
		val start = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_start", obj)

		storeNbtToScore(srcLen, libStorageArg, heapPath(macros.srcName))
		scoreOperation(findBound, Operation.SET, srcLen)
		scoreOperation(findBound, Operation.REMOVE, findSubLen)
		scoreOperation(findI, Operation.SET, start)
		findResult.set(this, -1)
		ifScoreCompareRunFunction(findI, Relation.LESS_THAN_OR_EQUAL_TO, findBound, OopConstants.stringFindMacroName)

		addLine(
			"execute unless score ${findResult.holder} $obj matches -1 " +
				"run scoreboard players remove ${cap.holder} $obj 1"
		)
		addLine(
			"execute unless score ${findResult.holder} $obj matches -1 " +
				"if score ${cap.holder} $obj matches 0.. " +
				"run function ${datapack.name}:${OopConstants.stringReplaceStepMacroName} " +
				"with storage $libStorage $stepArgs"
		)
		addLine(
			"execute unless score ${findResult.holder} $obj matches -1 " +
				"if score ${cap.holder} $obj matches 0.. " +
				"run scoreboard players operation ${start.holder} $obj = ${findResult.holder} $obj"
		)
		addLine(
			"execute unless score ${findResult.holder} $obj matches -1 " +
				"if score ${cap.holder} $obj matches 0.. " +
				"run scoreboard players operation ${start.holder} $obj += ${newLen.holder} $obj"
		)
		addLine(
			"execute unless score ${findResult.holder} $obj matches -1 " +
				"if score ${cap.holder} $obj matches 1.. " +
				"run function ${datapack.name}:${OopConstants.stringReplaceMacroName} " +
				"with storage $libStorage $controllerArgs"
		)
	}

private fun DynamicString.beginReplace(fn: Function, oldValue: String, newValue: String, cap: Int) {
	require(oldValue.isNotEmpty()) { "replace oldValue must not be empty." }
	val obj = runtime.config.lengthObjective
	runtime.replaceControllerHelper()
	val controllerArgs = runtime.argsPath(OopConstants.stringReplaceMacroName)
	val findArgs = runtime.argsPath(OopConstants.stringFindStepMacroName)
	val needlePath = runtime.tmpPath(REPLACE_NEEDLE_KEY)
	val stepArgs = runtime.argsPath(OopConstants.stringReplaceStepMacroName)

	fn.data(runtime.libStorageArg) {
		modify(runtime.tmpPath(REPLACE_NEW_KEY), newValue)
		modify(needlePath, oldValue)
		modify("$findArgs.src", name)
		modify("$findArgs.needlePath", needlePath)
		modify("$controllerArgs.srcName", name)
		modify("$stepArgs.srcName", name)
	}
	ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj).set(fn, oldValue.length)
	ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_cap", obj).set(fn, cap)
	ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_newlen", obj).set(fn, newValue.length)
	ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_start", obj).set(fn, 0)
	fn.callMacro(OopConstants.stringReplaceMacroName, runtime.libStorageArg, controllerArgs)
}

/** Replaces every occurrence of [oldValue] with [newValue] inside this string (in place). */
context(fn: Function)
fun DynamicString.replace(oldValue: String, newValue: String) = beginReplace(fn, oldValue, newValue, Int.MAX_VALUE)

/** Replaces only the first occurrence of [oldValue] with [newValue] inside this string. */
context(fn: Function)
fun DynamicString.replaceFirst(oldValue: String, newValue: String) = beginReplace(fn, oldValue, newValue, 1)

/** Replaces the content of `this[start, end)` with [replacement] in place. */
context(fn: Function)
fun DynamicString.replaceRange(start: Int, end: Int, replacement: String) {
	require(start in 0..end) { "replaceRange: invalid range [$start, $end)" }
	val after = runtime.scratchString(REPLACE_AFTER)
	val before = runtime.scratchString(REPLACE_BEFORE)
	substringTo(before, 0, start)
	substringTo(after, end)
	setFrom(before)
	append(replacement)
	appendFrom(after)
}

/** Dynamic variant accepting a [DynamicString] as replacement. */
context(fn: Function)
fun DynamicString.replaceRange(start: Int, end: Int, replacement: DynamicString) {
	require(start in 0..end) { "replaceRange: invalid range [$start, $end)" }
	val after = runtime.scratchString(REPLACE_AFTER)
	val before = runtime.scratchString(REPLACE_BEFORE)
	substringTo(before, 0, start)
	substringTo(after, end)
	setFrom(before)
	appendFrom(replacement)
	appendFrom(after)
}
