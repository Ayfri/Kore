package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

/** Macro identifiers for the `kore_string_find_step` helper. */
class FindStepMacros internal constructor() : Macros() {
	val end by "end"
	val needlePath by "needlePath"
	val src by "src"
	val start by "start"
}

/** Empty macros holder for the find controller (loop uses score state). */
class FindMacros internal constructor() : Macros()

/** Slot holding the extracted candidate substring during a find iteration. */
internal const val FIND_CANDIDATE_SLOT = "${INTERNAL_NAME_PREFIX}find_candidate"
internal const val FIND_EQ_KEY = "${INTERNAL_NAME_PREFIX}find_eq"

/** Default score holder storing the result of a find (-1 when absent). */
const val FIND_RESULT_HOLDER = "#${INTERNAL_NAME_PREFIX}find"

/** Default score holder storing the result of a contains check (0 / 1). */
const val CONTAINS_RESULT_HOLDER = "#${INTERNAL_NAME_PREFIX}contains"

/**
 * Registers the atomic find-step macro that extracts a candidate substring and compares it with
 * the needle, storing a 0/1 score into `#kore_string_diff` on `kore_string_len`.
 */
internal fun DynamicStringRuntime.findStepHelper(): FunctionWithMacros<FindStepMacros> =
	ensure(OopConstants.stringFindStepMacroName, ::FindStepMacros) {
		setSubstringMacro(
			storage = libStorageArg,
			path = heapPath(FIND_CANDIDATE_SLOT),
			srcStorage = libStorageArg,
			srcPath = heapPath(macros.src),
			startExpr = macros.start,
			endExpr = macros.end,
		)
		val eqPath = tmpPath(FIND_EQ_KEY)
		copyNbt(libStorageArg, eqPath, libStorageArg, macros.needlePath)
		addLine(
			"execute store success score #${INTERNAL_NAME_PREFIX}diff ${config.lengthObjective} run " +
				"data modify storage $libStorage $eqPath set from " +
				"storage $libStorage ${heapPath(FIND_CANDIDATE_SLOT)}"
		)
	}

/** Registers the recursive find controller that loops from `#i = 0` up to `#bound` inclusive. */
internal fun DynamicStringRuntime.findControllerHelper(): FunctionWithMacros<FindMacros> =
	ensure(OopConstants.stringFindMacroName, ::FindMacros) {
		findStepHelper()
		val findArgs = argsPath(OopConstants.stringFindStepMacroName)
		val iCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_i", config.lengthObjective)
		val ipSub = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_ipsub", config.lengthObjective)
		val subLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", config.lengthObjective)
		val diff = ScoreCursor("#${INTERNAL_NAME_PREFIX}diff", config.lengthObjective)
		val result = ScoreCursor(FIND_RESULT_HOLDER, config.lengthObjective)
		val bound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", config.lengthObjective)

		storeScoreToNbt(iCursor, libStorageArg, "$findArgs.start")
		scoreOperation(ipSub, Operation.SET, iCursor)
		scoreOperation(ipSub, Operation.ADD, subLen)
		storeScoreToNbt(ipSub, libStorageArg, "$findArgs.end")
		callMacro(OopConstants.stringFindStepMacroName, libStorageArg, findArgs)
		addLine(
			"execute if score ${diff.holder} ${diff.objective} matches 0 " +
				"run scoreboard players operation ${result.holder} ${result.objective} = " +
				"${iCursor.holder} ${iCursor.objective}"
		)
		iCursor.add(this, 1)
		addLine(
			"execute if score ${result.holder} ${result.objective} matches -1 " +
				"if score ${iCursor.holder} ${iCursor.objective} <= ${bound.holder} ${bound.objective} " +
				"run function ${datapack.name}:${OopConstants.stringFindMacroName}"
		)
	}

private data class FindPrelude(
	val bound: ScoreCursor,
	val i: ScoreCursor,
	val result: ScoreCursor,
	val srcLen: ScoreCursor,
	val subLen: ScoreCursor,
)

private fun DynamicString.preparePrelude(
	fn: Function,
	needlePath: String,
	needleLen: Int?,
): FindPrelude {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val obj = rt.config.lengthObjective
	val srcLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_srclen", obj)
	val subLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
	val bound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", obj)
	val i = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_i", obj)
	val result = ScoreCursor(FIND_RESULT_HOLDER, obj)

	context(fn) { length(srcLen.holder) }

	if (needleLen != null) subLen.set(fn, needleLen)
	else subLen.storeValueOfNbt(fn, rt.libStorageArg, needlePath)

	bound.assignFrom(fn, srcLen)
	bound.subFrom(fn, subLen)
	i.set(fn, 0)
	result.set(fn, -1)

	val findArgs = rt.argsPath(OopConstants.stringFindStepMacroName)
	fn.data(rt.libStorageArg) {
		modify("$findArgs.src", name)
		modify("$findArgs.needlePath", needlePath)
	}

	return FindPrelude(bound = bound, i = i, result = result, srcLen = srcLen, subLen = subLen)
}

/**
 * Looks for the first occurrence of the literal [needle] inside this string. Stores the result
 * index (or `-1`) into [resultHolder] on the `kore_string_len` objective and returns it.
 *
 * ```
 * "minecraft".indexOf("craft")  // 4
 * "minecraft".indexOf("kore")   // -1
 * ```
 */
context(fn: Function)
fun DynamicString.indexOf(needle: String, resultHolder: String = FIND_RESULT_HOLDER): DynamicStringResult {
	require(needle.isNotEmpty()) { "indexOf needle must not be empty." }
	val rt = fn.datapack.requireDynamicStringRuntime()
	val controller = rt.findControllerHelper()
	val needleScratch = rt.tmpPath("${INTERNAL_NAME_PREFIX}find_needle")
	fn.data(rt.libStorageArg) { modify(needleScratch, needle) }
	val prelude = preparePrelude(fn, needleScratch, needle.length)
	fn.ifScoreMatchesRunFunction(prelude.bound, rangeOrIntStart(0), controller.name)
	if (resultHolder != prelude.result.holder) {
		ScoreCursor(resultHolder, prelude.result.objective).assignFrom(fn, prelude.result)
	}
	return DynamicStringResult(resultHolder, prelude.result.objective)
}

/**
 * Dynamic variant looking for the content of another [DynamicString] as the needle.
 *
 * ```
 * // needle holds "craft" at runtime
 * "minecraft".indexOf(needle)  // 4
 * ```
 */
context(fn: Function)
fun DynamicString.indexOf(needle: DynamicString, resultHolder: String = FIND_RESULT_HOLDER): DynamicStringResult {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val controller = rt.findControllerHelper()
	val prelude = preparePrelude(fn, needle.nbtPath, null)
	fn.ifScoreMatchesRunFunction(prelude.bound, rangeOrIntStart(0), controller.name)
	if (resultHolder != prelude.result.holder) {
		ScoreCursor(resultHolder, prelude.result.objective).assignFrom(fn, prelude.result)
	}
	return DynamicStringResult(resultHolder, prelude.result.objective)
}

/** Internal helper turning the find result into a 0 / 1 flag. */
private fun Function.materialiseContainsFlag(resultHolder: String) {
	val obj = datapack.requireDynamicStringRuntime().config.lengthObjective
	val flag = ScoreCursor(resultHolder, obj)
	val result = ScoreCursor(FIND_RESULT_HOLDER, obj)
	flag.set(this, 0)
	execute {
		ifCondition { score(result.asScoreHolder(), obj, rangeOrIntStart(0)) }
		run { scoreboard { players { set(flag.asScoreHolder(), obj, 1) } } }
	}
}

/**
 * `true` (score 1) when [needle] exists in this string, `false` (score 0) otherwise.
 *
 * ```
 * "minecraft".contains("craft")  // 1
 * "minecraft".contains("kore")   // 0
 * ```
 */
context(fn: Function)
infix fun DynamicString.contains(needle: String): DynamicStringResult {
	val resultHolder = CONTAINS_RESULT_HOLDER
	indexOf(needle)
	fn.materialiseContainsFlag(resultHolder)
	return DynamicStringResult(resultHolder, runtime.config.lengthObjective)
}

/**
 * Dynamic variant of [contains] where [needle] is another [DynamicString].
 *
 * ```
 * // needle holds "craft" at runtime
 * "minecraft".contains(needle)  // 1
 * ```
 */
context(fn: Function)
infix fun DynamicString.contains(needle: DynamicString): DynamicStringResult {
	val resultHolder = CONTAINS_RESULT_HOLDER
	indexOf(needle)
	fn.materialiseContainsFlag(resultHolder)
	return DynamicStringResult(resultHolder, runtime.config.lengthObjective)
}
