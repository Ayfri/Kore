package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros

/** Empty macros holder used by the reverse tail function (the loop uses score-based state). */
class ReverseMacros internal constructor() : Macros()

/** Scratch heap slot used to extract a single character each reverse iteration. */
private const val REVERSE_ACCUMULATOR = "kore_string_rev_out"
private const val REVERSE_SCRATCH = "kore_string_reverse_scratch"

/**
 * Emits the re-entrant tail function that consumes one character per iteration. Relies on the
 * substring and concat macro functions being registered.
 */
internal fun DynamicStringRuntime.reverseTailHelper(): FunctionWithMacros<ReverseMacros> =
	ensure(OopConstants.stringReverseMacroName, ::ReverseMacros) {
		substringHelper()
		concatHelper()

		val subArgs = argsPath(OopConstants.stringSubstringMacroName)
		val cArgs = argsPath(OopConstants.stringConcatMacroName)
		val iCursor = ScoreCursor("#kore_string_rev_i", config.lengthObjective)
		val ip1 = ScoreCursor("#kore_string_rev_ip1", config.lengthObjective)

		storeScoreToNbt(iCursor, libStorageArg, "$subArgs.start")
		scoreOperation(ip1, Operation.SET, iCursor)
		ip1.add(this, 1)
		storeScoreToNbt(ip1, libStorageArg, "$subArgs.end")

		setNbtString(libStorageArg, "$subArgs.dst", REVERSE_SCRATCH)
		callMacro(OopConstants.stringSubstringMacroName, libStorageArg, subArgs)

		copyNbt(libStorageArg, "$cArgs.a", libStorageArg, heapPath(REVERSE_ACCUMULATOR))
		copyNbt(libStorageArg, "$cArgs.b", libStorageArg, heapPath(REVERSE_SCRATCH))
		setNbtString(libStorageArg, "$cArgs.dst", REVERSE_ACCUMULATOR)
		callMacro(OopConstants.stringConcatMacroName, libStorageArg, cArgs)

		iCursor.sub(this, 1)
		ifScoreMatchesRunFunction(iCursor, rangeOrIntStart(0), OopConstants.stringReverseMacroName)
	}

/**
 * Reverses the content of this string and writes the result into [target] (defaults to `this`).
 *
 * Implemented as a recursive tail function that walks the string from its last character down to
 * the first one, appending each character to an accumulator. The depth of the recursion matches
 * the length of the source string and is therefore bounded by Minecraft's
 * [Gamerules.MAX_COMMAND_SEQUENCE_LENGTH](https://minecraft.wiki/w/Game_rule#Miscellaneous) limit (65 536 by default).
 */
context(fn: Function)
fun DynamicString.reverse(target: DynamicString = this) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val tail = rt.reverseTailHelper()

	val accumulator = DynamicString(REVERSE_ACCUMULATOR)
	accumulator.set("")

	val subArgs = rt.argsPath(OopConstants.stringSubstringMacroName)
	fn.data(rt.libStorageArg) { modify("$subArgs.src", name) }

	val iCursor = ScoreCursor("#kore_string_rev_i", rt.config.lengthObjective)
	val lenCursor = ScoreCursor("#kore_string_rev_len", rt.config.lengthObjective)
	length(lenCursor.holder)
	iCursor.assignFrom(fn, lenCursor)
	iCursor.sub(fn, 1)

	fn.ifScoreMatchesRunFunction(iCursor, rangeOrIntStart(0), tail.name)

	if (target != accumulator) target.setFrom(accumulator)
}
