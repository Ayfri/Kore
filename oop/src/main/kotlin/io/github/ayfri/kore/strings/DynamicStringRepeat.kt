package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

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

internal fun DynamicStringRuntime.repeatStepHelper(): FunctionWithMacros<RepeatStepMacros> =
	ensure(OopConstants.stringRepeatStepMacroName, ::RepeatStepMacros) {
		concatHelper()
		val cArgs = argsPath(OopConstants.stringConcatMacroName)
		copyNbt(libStorageArg, "$cArgs.a", libStorageArg, heapPath(macros.dst))
		copyNbt(libStorageArg, "$cArgs.b", libStorageArg, heapPath(macros.src))
		setNbtString(libStorageArg, "$cArgs.dst", macros.dst)
		callMacro(OopConstants.stringConcatMacroName, libStorageArg, cArgs)
	}

internal fun DynamicStringRuntime.repeatControllerHelper(): FunctionWithMacros<RepeatMacros> =
	ensure(OopConstants.stringRepeatMacroName, ::RepeatMacros) {
		repeatStepHelper()
		val cap = ScoreCursor("#kore_string_repeat_cap", config.lengthObjective)
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
 * Repeats this string [times] times in place. Equivalent to Kotlin's `String.repeat(n)`.
 *
 * A static value 0 clears the string, negative values throw.
 */
context(fn: Function)
fun DynamicString.repeat(times: Int, target: DynamicString = this) {
	require(times >= 0) { "repeat count must be non negative, got $times" }
	val rt = fn.datapack.requireDynamicStringRuntime()
	if (times == 0) {
		target.set("")
		return
	}
	val srcBuf = DynamicString("kore_string_repeat_src")
	srcBuf.setFrom(this)
	target.setFrom(srcBuf)
	if (times == 1) return

	rt.repeatControllerHelper()

	val controllerArgs = rt.argsPath(OopConstants.stringRepeatMacroName)
	val stepArgs = rt.argsPath(OopConstants.stringRepeatStepMacroName)
	fn.data(rt.libStorageArg) {
		modify("$stepArgs.src", srcBuf.name)
		modify("$stepArgs.dst", target.name)
		modify("$controllerArgs.src", srcBuf.name)
		modify("$controllerArgs.dst", target.name)
	}
	ScoreCursor("#kore_string_repeat_cap", rt.config.lengthObjective).set(fn, times - 1)
	fn.callMacro(OopConstants.stringRepeatMacroName, rt.libStorageArg, controllerArgs)
}
