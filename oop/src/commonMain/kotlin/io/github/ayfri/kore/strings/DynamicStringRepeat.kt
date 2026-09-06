package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue
import io.github.ayfri.kore.scoreboard.ScoreboardEntity

private const val REPEAT_BUFFER_KEY = "${INTERNAL_NAME_PREFIX}repeat_buf"
private const val REPEAT_SRC_SCRATCH = "${INTERNAL_NAME_PREFIX}repeat_src"

/** Macros for the `kore_string_repeat_step` helper. */
class RepeatStepMacros internal constructor() : Macros() {
	val dst by "dst"
	val src by "src"
}

/** Macros holder used by the repeat controller. */
class RepeatMacros internal constructor() : Macros() {
	val dst by "dst"
	val src by "src"
}

/**
 * Appends one copy of `src` to `dst`. The source is staged in a scratch slot first so a caller may
 * legally pass the same slot as both operands without the destination growing while it is read.
 */
internal fun DynamicStringRuntime.repeatStepHelper(): FunctionWithMacros<RepeatStepMacros> =
	ensure(OopConstants.stringRepeatStepMacroName, ::RepeatStepMacros) {
		val buffer = tmpPath(REPEAT_BUFFER_KEY)
		copyNbt(libStorageArg, buffer, libStorageArg, heapPath(macros.src))
		addLine("data modify storage $libStorage ${heapPath(macros.dst)} append string storage $libStorage $buffer")
	}

internal fun DynamicStringRuntime.repeatControllerHelper(): FunctionWithMacros<RepeatMacros> =
	ensure(OopConstants.stringRepeatMacroName, ::RepeatMacros) {
		repeatStepHelper()
		val cap = ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", config.lengthObjective)
		val stepArgs = argsPath(OopConstants.stringRepeatStepMacroName)
		val controllerArgs = argsPath(OopConstants.stringRepeatMacroName)

		ifScoreMatchesRunMacro(
			cursor = cap,
			range = rangeOrIntStart(1),
			name = OopConstants.stringRepeatStepMacroName,
			storage = libStorageArg,
			path = stepArgs,
		)
		cap.sub(this, 1)
		ifScoreMatchesRunMacro(
			cursor = cap,
			range = rangeOrIntStart(1),
			name = OopConstants.stringRepeatMacroName,
			storage = libStorageArg,
			path = controllerArgs,
		)
	}

/**
 * Runs the repeat controller with a runtime count, growing [target] by one copy of [unit] per
 * iteration. [unit] has to be a slot of its own: reusing [target] as the source would double the
 * content on every step instead of adding a single copy.
 */
internal fun repeatInto(
	fn: Function,
	target: DynamicString,
	unit: DynamicString,
	cap: ScoreCursor,
	guarded: Boolean = true,
) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.repeatControllerHelper()
	val capCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", cap.objective)
	val controllerArgs = rt.argsPath(OopConstants.stringRepeatMacroName)
	val stepArgs = rt.argsPath(OopConstants.stringRepeatStepMacroName)

	fn.data(rt.libStorageArg) {
		modify("$stepArgs.src", unit.name)
		modify("$stepArgs.dst", target.name)
		modify("$controllerArgs.src", unit.name)
		modify("$controllerArgs.dst", target.name)
	}
	if (capCursor != cap) capCursor.assignFrom(fn, cap)
	if (!guarded) {
		fn.callMacro(OopConstants.stringRepeatMacroName, rt.libStorageArg, controllerArgs)
		return
	}
	fn.ifScoreMatchesRunMacro(
		cursor = capCursor,
		range = rangeOrIntStart(1),
		name = OopConstants.stringRepeatMacroName,
		storage = rt.libStorageArg,
		path = controllerArgs,
	)
}

/**
 * Repeats this string [times] times in place. Equivalent to Kotlin's `String.repeat(n)`.
 *
 * A static value 0 clears the string, negative values throw.
 *
 * ```
 * "ab".repeat(3)  // "ababab"
 * "ab".repeat(0)  // ""
 * ```
 */
context(fn: Function)
fun DynamicString.repeat(times: Int, target: DynamicString = this): DynamicString {
	require(times >= 0) { "repeat count must be non negative, got $times" }
	val rt = fn.datapack.requireDynamicStringRuntime()
	if (times == 0) {
		target.set("")
		return target
	}
	val srcBuf = rt.scratchString(REPEAT_SRC_SCRATCH)
	srcBuf.setFrom(this)
	target.setFrom(srcBuf)
	if (times == 1) return target

	val cap = ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", rt.config.lengthObjective)
	cap.set(fn, times - 1)
	repeatInto(fn, target, srcBuf, cap, guarded = false)
	return target
}

/**
 * Repeats this string as many times as the runtime value of [times], so a pack can build a progress
 * bar, an indentation or a separator whose width is computed in game.
 *
 * A count of `0` or less leaves [target] empty.
 *
 * ```
 * // count holds 3 at runtime
 * "|".repeat(count)  // "|||"
 * ```
 */
context(fn: Function)
fun DynamicString.repeat(times: ScoreboardEntity, target: DynamicString = this): DynamicString {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val obj = rt.config.lengthObjective
	val cap = ScoreCursor("#${INTERNAL_NAME_PREFIX}repeat_cap", obj)
	val srcBuf = rt.scratchString(REPEAT_SRC_SCRATCH)

	srcBuf.setFrom(this)
	target.set("")
	cap.assignFrom(fn, times)
	fn.execute {
		ifCondition { score(cap.asScoreHolder(), obj, rangeOrIntStart(1)) }
		run { data(rt.libStorageArg) { modify(target.nbtPath) { set(rt.libStorageArg, srcBuf.nbtPath) } } }
	}
	cap.sub(fn, 1)
	repeatInto(fn, target, srcBuf, cap)
	return target
}

/**
 * Operator alias of [repeat] with a static count.
 *
 * ```
 * separator *= 20  // "-" becomes "--------------------"
 * ```
 */
context(fn: Function)
operator fun DynamicString.timesAssign(times: Int) {
	repeat(times)
}

/** Operator alias of [repeat] with a runtime count. See [timesAssign]. */
context(fn: Function)
operator fun DynamicString.timesAssign(times: ScoreboardEntity) {
	repeat(times)
}

/** Expression form of [repeat]: writes the repeated value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.repeated(times: Int): DynamicString = repeat(times, runtime.tempString())

/** Expression form of [repeat] with a runtime count. See [repeated]. */
context(fn: Function)
fun DynamicString.repeated(times: ScoreboardEntity): DynamicString = repeat(times, runtime.tempString())
