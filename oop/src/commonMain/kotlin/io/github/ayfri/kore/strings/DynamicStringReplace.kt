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

private fun DynamicString.beginReplace(fn: Function, oldValue: StringPart, newValue: StringPart, cap: Int) {
	if (oldValue is StringPart.Literal) require(oldValue.value.isNotEmpty()) { "replace oldValue must not be empty." }
	val obj = runtime.config.lengthObjective
	val controller = runtime.replaceControllerHelper()
	val controllerArgs = runtime.argsPath(OopConstants.stringReplaceMacroName)
	val findArgs = runtime.argsPath(OopConstants.stringFindStepMacroName)
	val needlePath = runtime.tmpPath(REPLACE_NEEDLE_KEY)
	val newPath = runtime.tmpPath(REPLACE_NEW_KEY)
	val stepArgs = runtime.argsPath(OopConstants.stringReplaceStepMacroName)
	val needleLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
	val newLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_newlen", obj)

	runtime.writePart(fn, newValue, newPath)
	runtime.writePart(fn, oldValue, needlePath)
	fn.data(runtime.libStorageArg) {
		modify("$findArgs.src", name)
		modify("$findArgs.needlePath", needlePath)
		modify("$controllerArgs.srcName", name)
		modify("$stepArgs.srcName", name)
	}
	runtime.writePartLength(fn, oldValue, needlePath, needleLen)
	ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_cap", obj).set(fn, cap)
	runtime.writePartLength(fn, newValue, newPath, newLen)
	ScoreCursor("#${INTERNAL_NAME_PREFIX}replace_start", obj).set(fn, 0)

	// A runtime needle can be empty, and an empty needle matches at the cursor forever, so the loop is guarded.
	if (oldValue is StringPart.Literal) fn.callMacro(controller.name, runtime.libStorageArg, controllerArgs)
	else fn.ifScoreMatchesRunMacro(needleLen, rangeOrIntStart(1), controller.name, runtime.libStorageArg, controllerArgs)
}

/**
 * Replaces every occurrence of [oldValue] with [newValue] inside this string (in place).
 *
 * ```
 * "a-b-c".replace("-", "+")  // "a+b+c"
 * "aa".replace("a", "aa")    // "aaaa", the search resumes past what it just wrote
 * ```
 */
context(fn: Function)
fun DynamicString.replace(oldValue: String, newValue: String) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, Int.MAX_VALUE)

/**
 * Replaces every occurrence of the runtime content of [oldValue]. An empty needle replaces nothing.
 *
 * ```
 * // needle holds "-" at runtime
 * "a-b".replace(needle, "+")  // "a+b"
 * ```
 */
context(fn: Function)
fun DynamicString.replace(oldValue: DynamicString, newValue: String) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, Int.MAX_VALUE)

/**
 * Replaces every occurrence of [oldValue] with the runtime content of [newValue].
 *
 * ```
 * // value holds "+" at runtime
 * "a-b".replace("-", value)  // "a+b"
 * ```
 */
context(fn: Function)
fun DynamicString.replace(oldValue: String, newValue: DynamicString) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, Int.MAX_VALUE)

/**
 * Fully dynamic replace: both the needle and the replacement are read at runtime.
 *
 * ```
 * // needle holds "-" and value holds "+" at runtime
 * "a-b".replace(needle, value)  // "a+b"
 * ```
 */
context(fn: Function)
fun DynamicString.replace(oldValue: DynamicString, newValue: DynamicString) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, Int.MAX_VALUE)

/**
 * Replaces only the first occurrence of [oldValue] with [newValue] inside this string.
 *
 * ```
 * "a-b-c".replaceFirst("-", "+")  // "a+b-c"
 * ```
 */
context(fn: Function)
fun DynamicString.replaceFirst(oldValue: String, newValue: String) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, 1)

/**
 * Replaces only the first occurrence of the runtime content of [oldValue].
 *
 * ```
 * // needle holds "-" at runtime
 * "a-b-c".replaceFirst(needle, "+")  // "a+b-c"
 * ```
 */
context(fn: Function)
fun DynamicString.replaceFirst(oldValue: DynamicString, newValue: String) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, 1)

/**
 * Replaces only the first occurrence of [oldValue] with the runtime content of [newValue].
 *
 * ```
 * // value holds "+" at runtime
 * "a-b-c".replaceFirst("-", value)  // "a+b-c"
 * ```
 */
context(fn: Function)
fun DynamicString.replaceFirst(oldValue: String, newValue: DynamicString) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, 1)

/**
 * Fully dynamic [replaceFirst]: both the needle and the replacement are read at runtime.
 *
 * ```
 * // needle holds "-" and value holds "+" at runtime
 * "a-b-c".replaceFirst(needle, value)  // "a+b-c"
 * ```
 */
context(fn: Function)
fun DynamicString.replaceFirst(oldValue: DynamicString, newValue: DynamicString) =
	beginReplace(fn, oldValue.asStringPart, newValue.asStringPart, 1)

/**
 * Replaces the content of `this[start, end)` with [replacement] in place.
 *
 * ```
 * "minecraft".replaceRange(0, 4, "war")  // "warcraft"
 * ```
 */
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

/**
 * Dynamic variant accepting a [DynamicString] as replacement.
 *
 * ```
 * // value holds "war" at runtime
 * "minecraft".replaceRange(0, 4, value)  // "warcraft"
 * ```
 */
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
