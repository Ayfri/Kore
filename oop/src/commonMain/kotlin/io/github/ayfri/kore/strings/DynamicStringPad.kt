package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.Function

private const val PAD_CAP_SCORE = "#${INTERNAL_NAME_PREFIX}pad_cap"
private const val PAD_DIFF_SCORE = "#${INTERNAL_NAME_PREFIX}pad_diff"
private const val PAD_LEN_SCORE = "#${INTERNAL_NAME_PREFIX}pad_len"
private const val PAD_SCRATCH = "${INTERNAL_NAME_PREFIX}pad_scratch"
private const val PAD_SRC_COPY = "${INTERNAL_NAME_PREFIX}pad_src"

/**
 * Runs the existing repeat controller dynamically. `scratch` is both the source and the destination
 * so `scratch` ends up containing its initial value repeated `diff` times when `#repeat_cap` is set
 * to `diff - 1` beforehand.
 */
private fun dynamicRepeatInPlace(fn: Function, scratch: DynamicString, cap: ScoreCursor) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.repeatControllerHelper()
	val controllerArgs = rt.argsPath(OopConstants.stringRepeatMacroName)
	val stepArgs = rt.argsPath(OopConstants.stringRepeatStepMacroName)

	fn.data(rt.libStorageArg) {
		modify("$stepArgs.src", scratch.name)
		modify("$stepArgs.dst", scratch.name)
		modify("$controllerArgs.src", scratch.name)
		modify("$controllerArgs.dst", scratch.name)
	}
	ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", cap.objective).assignFrom(fn, cap)
	fn.ifScoreMatchesRunMacro(
		cursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", cap.objective),
		range = rangeOrIntStart(1),
		name = OopConstants.stringRepeatMacroName,
		storage = rt.libStorageArg,
		path = controllerArgs,
	)
}

private fun applyPad(
	fn: Function,
	source: DynamicString,
	targetLength: Int,
	padChar: Char,
	target: DynamicString,
	prepend: Boolean,
) {
	require(targetLength >= 0) { "target length must be non negative, got $targetLength" }
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.concatHelper()
	val obj = rt.config.lengthObjective

	val srcCopy = rt.scratchString(PAD_SRC_COPY)
	context(fn) { srcCopy.setFrom(source) }

	val curLen = ScoreCursor(PAD_LEN_SCORE, obj)
	context(fn) { srcCopy.length(curLen.holder) }
	val diff = ScoreCursor(PAD_DIFF_SCORE, obj)
	diff.set(fn, targetLength)
	diff.subFrom(fn, curLen)

	context(fn) { target.setFrom(srcCopy) }
	if (targetLength == 0) return

	val scratch = rt.scratchString(PAD_SCRATCH)
	fn.data(rt.libStorageArg) { modify(scratch.nbtPath, padChar.toString()) }

	val capCursor = ScoreCursor(PAD_CAP_SCORE, obj)
	capCursor.assignFrom(fn, diff)
	capCursor.sub(fn, 1)
	dynamicRepeatInPlace(fn, scratch, capCursor)

	val cArgs = rt.argsPath(OopConstants.stringConcatMacroName)
	val leftName = if (prepend) scratch.name else srcCopy.name
	val rightName = if (prepend) srcCopy.name else scratch.name
	fn.data(rt.libStorageArg) { modify("$cArgs.dst", target.name) }

	val diffCond = "if score ${diff.holder} $obj matches 1.."
	fn.addLine(
		"execute $diffCond run data modify storage ${rt.libStorage} $cArgs.a " +
			"set from storage ${rt.libStorage} ${rt.heapPath(leftName)}"
	)
	fn.addLine(
		"execute $diffCond run data modify storage ${rt.libStorage} $cArgs.b " +
			"set from storage ${rt.libStorage} ${rt.heapPath(rightName)}"
	)
	fn.addLine(
		"execute $diffCond run function ${fn.datapack.name}:${OopConstants.stringConcatMacroName} " +
			"with storage ${rt.libStorage} $cArgs"
	)
}

/**
 * Kotlin-style `padStart`: if the current length is strictly smaller than [targetLength], prepends
 * enough copies of [padChar] so the total reaches [targetLength]. Otherwise [target] receives a
 * copy of this string unchanged.
 */
context(fn: Function)
fun DynamicString.padStart(targetLength: Int, padChar: Char = ' ', target: DynamicString = this) =
	applyPad(fn, this, targetLength, padChar, target, prepend = true)

/**
 * Kotlin-style `padEnd`: if the current length is strictly smaller than [targetLength], appends
 * enough copies of [padChar] so the total reaches [targetLength].
 */
context(fn: Function)
fun DynamicString.padEnd(targetLength: Int, padChar: Char = ' ', target: DynamicString = this) =
	applyPad(fn, this, targetLength, padChar, target, prepend = false)
