package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

/** Macros for the `kore_string_split_step` helper. */
class SplitStepMacros internal constructor() : Macros() {
	val listPath by "listPath"
}

/** Empty macros holder for the split controller. */
class SplitMacros internal constructor() : Macros()

private const val SPLIT_SCRATCH = "${INTERNAL_NAME_PREFIX}split_scratch"

/**
 * Step helper that:
 * 1. Calls the find controller to locate the next delimiter occurrence at or after `#split_start`.
 * 2. Extracts the substring `src[#split_start .. #find]` (or `src[#split_start .. #src_len]` if no match).
 * 3. Appends the extracted substring to the target list.
 * 4. Updates `#split_start` and recurses when a delimiter was found.
 */
internal fun DynamicStringRuntime.splitStepHelper(): FunctionWithMacros<SplitStepMacros> =
	ensure(OopConstants.stringSplitStepMacroName, ::SplitStepMacros) {
		findControllerHelper()
		substringHelper()
		val obj = config.lengthObjective
		val subArgs = argsPath(OopConstants.stringSubstringMacroName)

		val splitStart = ScoreCursor("#${INTERNAL_NAME_PREFIX}split_start", obj)
		val splitEnd = ScoreCursor("#${INTERNAL_NAME_PREFIX}split_end", obj)
		val splitSrcLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}split_srclen", obj)
		val findI = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_i", obj)
		val findBound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", obj)
		val findResult = ScoreCursor(FIND_RESULT_HOLDER, obj)
		val findSubLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)

		scoreOperation(findI, Operation.SET, splitStart)
		findResult.set(this, -1)
		ifScoreCompareRunFunction(findI, Relation.LESS_THAN_OR_EQUAL_TO, findBound, OopConstants.stringFindMacroName)

		scoreOperation(splitEnd, Operation.SET, splitSrcLen)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. run " +
				"scoreboard players operation ${splitEnd.holder} $obj = ${findResult.holder} $obj"
		)

		storeScoreToNbt(splitStart, libStorageArg, "$subArgs.start")
		storeScoreToNbt(splitEnd, libStorageArg, "$subArgs.end")
		callMacro(OopConstants.stringSubstringMacroName, libStorageArg, subArgs)

		addLine(
			"data modify storage $libStorage ${macros.listPath} append from " +
				"storage $libStorage ${heapPath(SPLIT_SCRATCH)}"
		)

		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. run " +
				"scoreboard players operation ${splitStart.holder} $obj = ${findResult.holder} $obj"
		)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. run " +
				"scoreboard players operation ${splitStart.holder} $obj += ${findSubLen.holder} $obj"
		)
		addLine(
			"execute if score ${findResult.holder} $obj matches 0.. " +
				"if score ${splitStart.holder} $obj <= ${splitSrcLen.holder} $obj run " +
				"function ${datapack.name}:${OopConstants.stringSplitStepMacroName} " +
				"with storage $libStorage ${argsPath(OopConstants.stringSplitStepMacroName)}"
		)
	}

/**
 * Splits this string on the given literal [delimiter] and writes the resulting tokens into [target].
 *
 * Clears [target] first. Empty tokens are preserved when two delimiters are adjacent or at the
 * string boundaries (Kotlin semantics).
 */
context(fn: Function)
fun DynamicString.split(delimiter: String, target: KoreStringList) {
	require(delimiter.isNotEmpty()) { "split delimiter must not be empty." }
	val rt = fn.datapack.requireDynamicStringRuntime()
	val step = rt.splitStepHelper()
	target.clear()

	val obj = rt.config.lengthObjective
	val delimPath = rt.tmpPath("${INTERNAL_NAME_PREFIX}split_delim")
	val findArgs = rt.argsPath(OopConstants.stringFindStepMacroName)
	val stepArgs = rt.argsPath(OopConstants.stringSplitStepMacroName)
	val subArgs = rt.argsPath(OopConstants.stringSubstringMacroName)

	fn.data(rt.libStorageArg) {
		modify(delimPath, delimiter)
		modify("$findArgs.src", name)
		modify("$findArgs.needlePath", delimPath)
		modify("$stepArgs.listPath", target.nbtPath)
		modify("$subArgs.src", name)
		modify("$subArgs.dst", SPLIT_SCRATCH)
	}

	val srcLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}split_srclen", obj)
	val findSubLen = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_sublen", obj)
	val findBound = ScoreCursor("#${INTERNAL_NAME_PREFIX}find_bound", obj)
	val splitStart = ScoreCursor("#${INTERNAL_NAME_PREFIX}split_start", obj)

	length(srcLen.holder)
	findSubLen.set(fn, delimiter.length)
	findBound.assignFrom(fn, srcLen)
	findBound.sub(fn, delimiter.length)
	splitStart.set(fn, 0)

	fn.ifScoreCompareRunMacro(
		left = splitStart,
		relation = Relation.LESS_THAN_OR_EQUAL_TO,
		right = srcLen,
		name = step.name,
		storage = rt.libStorageArg,
		path = stepArgs,
	)
}
