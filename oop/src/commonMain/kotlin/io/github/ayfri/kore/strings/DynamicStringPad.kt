package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.commands.DataModifyOperation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function

private const val PAD_CAP_SCORE = "#${INTERNAL_NAME_PREFIX}pad_cap"
private const val PAD_CHAR_SCRATCH = "${INTERNAL_NAME_PREFIX}pad_char"
private const val PAD_DIFF_SCORE = "#${INTERNAL_NAME_PREFIX}pad_diff"
private const val PAD_LEN_SCORE = "#${INTERNAL_NAME_PREFIX}pad_len"
private const val PAD_SCRATCH = "${INTERNAL_NAME_PREFIX}pad_scratch"
private const val PAD_SRC_COPY = "${INTERNAL_NAME_PREFIX}pad_src"

/**
 * Runs the repeat controller with a runtime count, growing [scratch] by one copy of [unit] per
 * iteration. [unit] has to be a slot of its own: reusing [scratch] as the source would double the
 * padding on every step instead of adding a single character.
 */
private fun dynamicRepeatInto(fn: Function, scratch: DynamicString, unit: DynamicString, cap: ScoreCursor) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.repeatControllerHelper()
	val capCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", cap.objective)
	val controllerArgs = rt.argsPath(OopConstants.stringRepeatMacroName)
	val stepArgs = rt.argsPath(OopConstants.stringRepeatStepMacroName)

	fn.data(rt.libStorageArg) {
		modify("$stepArgs.src", unit.name)
		modify("$stepArgs.dst", scratch.name)
		modify("$controllerArgs.src", unit.name)
		modify("$controllerArgs.dst", scratch.name)
	}
	capCursor.assignFrom(fn, cap)
	fn.ifScoreMatchesRunMacro(
		cursor = capCursor,
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

	val padUnit = rt.scratchString(PAD_CHAR_SCRATCH)
	val scratch = rt.scratchString(PAD_SCRATCH)
	fn.data(rt.libStorageArg) {
		modify(padUnit.nbtPath, padChar.toString())
		modify(scratch.nbtPath, padChar.toString())
	}

	val capCursor = ScoreCursor(PAD_CAP_SCORE, obj)
	capCursor.assignFrom(fn, diff)
	capCursor.sub(fn, 1)
	dynamicRepeatInto(fn, scratch, padUnit, capCursor)

	val glue: DataModifyOperation.() -> List<Argument> =
		if (prepend) {
			{ prepend(rt.libStorageArg, scratch.nbtPath, null, null) }
		} else {
			{ append(rt.libStorageArg, scratch.nbtPath, null, null) }
		}

	fn.padGlue(diff, obj, rt.libStorageArg, target.nbtPath, glue)
}

/** Appends or prepends the built padding only when the string was actually shorter than requested. */
private fun Function.padGlue(
	diff: ScoreCursor,
	obj: String,
	storage: StorageArgument,
	path: String,
	glue: DataModifyOperation.() -> List<Argument>,
) = execute {
	ifCondition { score(diff.asScoreHolder(), obj, rangeOrIntStart(1)) }
	run { data(storage) { modify(path, glue) } }
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
