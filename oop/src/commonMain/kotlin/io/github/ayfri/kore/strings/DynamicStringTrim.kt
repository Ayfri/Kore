package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

private const val TRIM_CHAR_KEY = "${INTERNAL_NAME_PREFIX}trim_c"
private const val TRIM_END_SCORE = "#${INTERNAL_NAME_PREFIX}trim_end"
private const val TRIM_I_SCORE = "#${INTERNAL_NAME_PREFIX}trim_i"
private const val TRIM_LEN_SCORE = "#${INTERNAL_NAME_PREFIX}trim_len"
private const val TRIM_WS_SCORE = "#${INTERNAL_NAME_PREFIX}trim_ws"

/** Macros for the character-extract step reused by trim-start / trim-end. */
class TrimStepMacros internal constructor() : Macros() {
	val i by "i"
	val iPlusOne by "iPlusOne"
	val src by "src"
}

/** Empty macros holder for trim controllers (loop state lives in scores). */
class TrimControllerMacros internal constructor() : Macros()

/**
 * Step macro extracting `src[i..i+1]` into the trim scratch slot and setting [TRIM_WS_SCORE] to `1`
 * when the character matches any of [DynamicStringConfig.trimWhitespace], `0` otherwise.
 */
private fun DynamicStringRuntime.registerTrimStep(name: String): FunctionWithMacros<TrimStepMacros> =
	ensure(name, ::TrimStepMacros) {
		val charPath = tmpPath(TRIM_CHAR_KEY)
		val obj = config.lengthObjective
		val wsScore = ScoreCursor(TRIM_WS_SCORE, obj)

		setSubstringMacro(
			storage = libStorageArg,
			path = charPath,
			srcStorage = libStorageArg,
			srcPath = heapPath(macros.src),
			startExpr = macros.i,
			endExpr = macros.iPlusOne,
		)
		wsScore.set(this, 0)
		for (ws in config.trimWhitespace) {
			addLine(
				"execute if data storage $libStorage ${config.tmpRoot}{$TRIM_CHAR_KEY:\"$ws\"} run " +
					"scoreboard players set ${wsScore.holder} $obj 1"
			)
		}
	}

internal fun DynamicStringRuntime.trimEndStepHelper(): FunctionWithMacros<TrimStepMacros> =
	registerTrimStep(OopConstants.stringTrimEndStepMacroName)

internal fun DynamicStringRuntime.trimStartStepHelper(): FunctionWithMacros<TrimStepMacros> =
	registerTrimStep(OopConstants.stringTrimStartStepMacroName)

/** Recursive controller advancing `#trim_i` while leading characters are whitespace. */
internal fun DynamicStringRuntime.trimStartControllerHelper(): FunctionWithMacros<TrimControllerMacros> =
	ensure(OopConstants.stringTrimStartMacroName, ::TrimControllerMacros) {
		val step = trimStartStepHelper()
		val obj = config.lengthObjective
		val stepArgs = argsPath(step.name)
		val iCursor = ScoreCursor(TRIM_I_SCORE, obj)
		val ip1 = ScoreCursor("#${INTERNAL_NAME_PREFIX}trim_ip1", obj)
		val endCursor = ScoreCursor(TRIM_END_SCORE, obj)
		val wsScore = ScoreCursor(TRIM_WS_SCORE, obj)

		storeScoreToNbt(iCursor, libStorageArg, "$stepArgs.i")
		scoreOperation(ip1, Operation.SET, iCursor)
		ip1.add(this, 1)
		storeScoreToNbt(ip1, libStorageArg, "$stepArgs.iPlusOne")
		callMacro(step.name, libStorageArg, stepArgs)
		addLine(
			"execute if score ${wsScore.holder} $obj matches 1 run " +
				"scoreboard players add ${iCursor.holder} $obj 1"
		)
		addLine(
			"execute if score ${wsScore.holder} $obj matches 1 " +
				"if score ${iCursor.holder} $obj < ${endCursor.holder} $obj run " +
				"function ${datapack.name}:${OopConstants.stringTrimStartMacroName}"
		)
	}

/** Recursive controller decrementing `#trim_end` while trailing characters are whitespace. */
internal fun DynamicStringRuntime.trimEndControllerHelper(): FunctionWithMacros<TrimControllerMacros> =
	ensure(OopConstants.stringTrimEndMacroName, ::TrimControllerMacros) {
		val step = trimEndStepHelper()
		val obj = config.lengthObjective
		val stepArgs = argsPath(step.name)
		val iCursor = ScoreCursor(TRIM_I_SCORE, obj)
		val last = ScoreCursor("#${INTERNAL_NAME_PREFIX}trim_last", obj)
		val endCursor = ScoreCursor(TRIM_END_SCORE, obj)
		val wsScore = ScoreCursor(TRIM_WS_SCORE, obj)

		scoreOperation(last, Operation.SET, endCursor)
		last.sub(this, 1)
		storeScoreToNbt(last, libStorageArg, "$stepArgs.i")
		storeScoreToNbt(endCursor, libStorageArg, "$stepArgs.iPlusOne")
		callMacro(step.name, libStorageArg, stepArgs)
		addLine(
			"execute if score ${wsScore.holder} $obj matches 1 run " +
				"scoreboard players remove ${endCursor.holder} $obj 1"
		)
		addLine(
			"execute if score ${wsScore.holder} $obj matches 1 " +
				"if score ${endCursor.holder} $obj > ${iCursor.holder} $obj run " +
				"function ${datapack.name}:${OopConstants.stringTrimEndMacroName}"
		)
	}

private fun prepareTrim(fn: Function, source: DynamicString, stepName: String) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val stepArgs = rt.argsPath(stepName)
	fn.data(rt.libStorageArg) { modify("$stepArgs.src", source.name) }
}

private data class TrimCursors(val end: ScoreCursor, val i: ScoreCursor, val len: ScoreCursor)

private fun DynamicString.primeTrim(fn: Function): TrimCursors {
	val obj = fn.datapack.requireDynamicStringRuntime().config.lengthObjective
	val len = ScoreCursor(TRIM_LEN_SCORE, obj)
	val iCursor = ScoreCursor(TRIM_I_SCORE, obj)
	val endCursor = ScoreCursor(TRIM_END_SCORE, obj)
	context(fn) { length(len.holder) }
	iCursor.set(fn, 0)
	endCursor.assignFrom(fn, len)
	return TrimCursors(end = endCursor, i = iCursor, len = len)
}

/**
 * Strips leading whitespace (configurable via [DynamicStringConfig.trimWhitespace]) into [target].
 *
 * ```
 * "  hi  ".trimStart()  // "hi  "
 * ```
 */
context(fn: Function)
fun DynamicString.trimStart(target: DynamicString = this) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val controller = rt.trimStartControllerHelper()
	val c = primeTrim(fn)
	prepareTrim(fn, this, OopConstants.stringTrimStartStepMacroName)
	fn.ifScoreCompareRunFunction(c.i, Relation.LESS_THAN, c.end, controller.name)
	substringDynamicCursors(fn, c.i, c.end, target)
}

/**
 * Strips trailing whitespace (configurable via [DynamicStringConfig.trimWhitespace]) into [target].
 *
 * ```
 * "  hi  ".trimEnd()  // "  hi"
 * ```
 */
context(fn: Function)
fun DynamicString.trimEnd(target: DynamicString = this) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val controller = rt.trimEndControllerHelper()
	val c = primeTrim(fn)
	prepareTrim(fn, this, OopConstants.stringTrimEndStepMacroName)
	fn.ifScoreCompareRunFunction(c.end, Relation.GREATER_THAN, c.i, controller.name)
	substringDynamicCursors(fn, c.i, c.end, target)
}

/**
 * Strips both leading and trailing whitespace.
 *
 * ```
 * "  hi  ".trim()  // "hi"
 * ```
 */
context(fn: Function)
fun DynamicString.trim(target: DynamicString = this) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val startController = rt.trimStartControllerHelper()
	val endController = rt.trimEndControllerHelper()
	val c = primeTrim(fn)
	prepareTrim(fn, this, OopConstants.stringTrimStartStepMacroName)
	prepareTrim(fn, this, OopConstants.stringTrimEndStepMacroName)
	fn.ifScoreCompareRunFunction(c.i, Relation.LESS_THAN, c.end, startController.name)
	fn.ifScoreCompareRunFunction(c.end, Relation.GREATER_THAN, c.i, endController.name)
	substringDynamicCursors(fn, c.i, c.end, target)
}
