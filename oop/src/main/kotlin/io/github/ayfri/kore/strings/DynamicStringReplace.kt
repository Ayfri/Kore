package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
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

/** Kept for symmetric runtime access (range replacements do not need a macro). */
class ReplaceRangeMacros internal constructor() : Macros()

private const val REPLACE_AFTER = "kore_string_replace_after"
private const val REPLACE_BEFORE = "kore_string_replace_before"
private const val REPLACE_NEEDLE_PATH = "tmp.kore_string_replace_needle"
private const val REPLACE_NEW_PATH = "tmp.kore_string_replace_new"

/** Replaces the content of `this[start, end)` with [replacement] in place. */
context(fn: Function)
fun DynamicString.replaceRange(start: Int, end: Int, replacement: String) {
	require(start in 0..end) { "replaceRange: invalid range [$start, $end)" }
	val before = DynamicString(REPLACE_BEFORE)
	val after = DynamicString(REPLACE_AFTER)
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
	val before = DynamicString(REPLACE_BEFORE)
	val after = DynamicString(REPLACE_AFTER)
	substringTo(before, 0, start)
	substringTo(after, end)
	setFrom(before)
	appendFrom(replacement)
	appendFrom(after)
}

/**
 * Macro step performing a single in-place replacement at the already-resolved position
 * `#kore_string_find`:
 *   1. `before = src[0 .. #find)`
 *   2. `after  = src[#find + oldLen .. srcLen)`
 *   3. `src    = before`, then append `tmp.kore_string_replace_new`, then append `after`.
 */
internal fun DynamicStringRuntime.replaceStepHelper(): FunctionWithMacros<ReplaceStepMacros> =
	ensure(OopConstants.stringReplaceStepMacroName, ::ReplaceStepMacros) {
		substringHelper()
		val obj = config.lengthObjective
		val subArgs = argsPath(OopConstants.stringSubstringMacroName)
		val zero = ScoreCursor("#kore_string_zero", obj)
		val findResult = ScoreCursor(FIND_RESULT_HOLDER, obj)
		val findSubLen = ScoreCursor("#kore_string_find_sublen", obj)
		val srcLen = ScoreCursor("#kore_string_replace_srclen", obj)
		val afterStart = ScoreCursor("#kore_string_replace_after_start", obj)

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
				append(libStorageArg, REPLACE_NEW_PATH)
			}
			modify(heapPath(macros.srcName)) {
				append(libStorageArg, heapPath(REPLACE_AFTER))
			}
		}
	}

/**
 * Replace controller loop: finds the next match, mutates the source with the replace-step macro
 * then decrements the `#kore_string_replace_cap` counter before re-entering the loop.
 */
internal fun DynamicStringRuntime.replaceControllerHelper(): FunctionWithMacros<ReplaceMacros> =
	ensure(OopConstants.stringReplaceMacroName, ::ReplaceMacros) {
		findControllerHelper()
		replaceStepHelper()
		val obj = config.lengthObjective
		val stepArgs = argsPath(OopConstants.stringReplaceStepMacroName)
		val controllerArgs = argsPath(OopConstants.stringReplaceMacroName)
		val srcLen = ScoreCursor("#kore_string_replace_srclen", obj)
		val findBound = ScoreCursor("#kore_string_find_bound", obj)
		val findSubLen = ScoreCursor("#kore_string_find_sublen", obj)
		val findI = ScoreCursor("#kore_string_find_i", obj)
		val findResult = ScoreCursor(FIND_RESULT_HOLDER, obj)
		val cap = ScoreCursor("#kore_string_replace_cap", obj)

		storeNbtToScore(srcLen, libStorageArg, heapPath(macros.srcName))
		scoreOperation(findBound, Operation.SET, srcLen)
		scoreOperation(findBound, Operation.REMOVE, findSubLen)
		findI.set(this, 0)
		findResult.set(this, -1)
		ifScoreMatchesRunFunction(findBound, rangeOrIntStart(0), OopConstants.stringFindMacroName)
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
				"if score ${cap.holder} $obj matches 1.. " +
				"run function ${datapack.name}:${OopConstants.stringReplaceMacroName} " +
				"with storage $libStorage $controllerArgs"
		)
	}

private fun DynamicString.beginReplace(fn: Function, oldValue: String, newValue: String, cap: Int) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.replaceControllerHelper()
	val findArgs = rt.argsPath(OopConstants.stringFindStepMacroName)
	val controllerArgs = rt.argsPath(OopConstants.stringReplaceMacroName)
	val stepArgs = rt.argsPath(OopConstants.stringReplaceStepMacroName)

	fn.data(rt.libStorageArg) {
		modify(REPLACE_NEW_PATH, newValue)
		modify(REPLACE_NEEDLE_PATH, oldValue)
		modify("$findArgs.src", name)
		modify("$findArgs.needlePath", REPLACE_NEEDLE_PATH)
		modify("$controllerArgs.srcName", name)
		modify("$stepArgs.srcName", name)
	}
	ScoreCursor("#kore_string_find_sublen", rt.config.lengthObjective).set(fn, oldValue.length)
	ScoreCursor("#kore_string_replace_cap", rt.config.lengthObjective).set(fn, cap)
	fn.callMacro(OopConstants.stringReplaceMacroName, rt.libStorageArg, controllerArgs)
}

/** Replaces every occurrence of [oldValue] with [newValue] inside this string (in place). */
context(fn: Function)
fun DynamicString.replace(oldValue: String, newValue: String) = beginReplace(fn, oldValue, newValue, Int.MAX_VALUE)

/** Replaces only the first occurrence of [oldValue] with [newValue] inside this string. */
context(fn: Function)
fun DynamicString.replaceFirst(oldValue: String, newValue: String) = beginReplace(fn, oldValue, newValue, 1)
