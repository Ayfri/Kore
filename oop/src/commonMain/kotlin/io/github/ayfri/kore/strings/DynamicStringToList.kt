package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

/** Macros for the atomic `kore_string_to_list_step` helper. */
class ToListStepMacros internal constructor() : Macros() {
	val i by "i"
	val iPlusOne by "iPlusOne"
	val listPath by "listPath"
	val src by "src"
}

/** Empty macros holder used by the to-list controller loop. */
class ToListMacros internal constructor() : Macros()

private const val TO_LIST_SCRATCH = "${INTERNAL_NAME_PREFIX}to_list_scratch"

internal fun DynamicStringRuntime.toListStepHelper(): FunctionWithMacros<ToListStepMacros> =
	ensure(OopConstants.stringToListStepMacroName, ::ToListStepMacros) {
		setSubstringMacro(
			storage = libStorageArg,
			path = heapPath(TO_LIST_SCRATCH),
			srcStorage = libStorageArg,
			srcPath = heapPath(macros.src),
			startExpr = macros.i,
			endExpr = macros.iPlusOne,
		)
		addLine(
			"data modify storage $libStorage ${macros.listPath} append from " +
				"storage $libStorage ${heapPath(TO_LIST_SCRATCH)}"
		)
	}

internal fun DynamicStringRuntime.toListControllerHelper(): FunctionWithMacros<ToListMacros> =
	ensure(OopConstants.stringToListMacroName, ::ToListMacros) {
		toListStepHelper()
		val stepArgs = argsPath(OopConstants.stringToListStepMacroName)
		val iCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}tl_i", config.lengthObjective)
		val ip1 = ScoreCursor("#${INTERNAL_NAME_PREFIX}tl_ip1", config.lengthObjective)
		val len = ScoreCursor("#${INTERNAL_NAME_PREFIX}tl_len", config.lengthObjective)

		storeScoreToNbt(iCursor, libStorageArg, "$stepArgs.i")
		scoreOperation(ip1, Operation.SET, iCursor)
		ip1.add(this, 1)
		storeScoreToNbt(ip1, libStorageArg, "$stepArgs.iPlusOne")
		callMacro(OopConstants.stringToListStepMacroName, libStorageArg, stepArgs)
		iCursor.add(this, 1)
		ifScoreCompareRunFunction(iCursor, Relation.LESS_THAN, len, OopConstants.stringToListMacroName)
	}

/**
 * Splits this string into one-character elements and writes them into [target], clearing any
 * previous content. Equivalent of `string.toCharArray().map { it.toString() }` converted to a
 * persistent NBT list of strings.
 *
 * ```
 * "kore".toList(out)  // out = ["k", "o", "r", "e"]
 * ```
 */
context(fn: Function)
fun DynamicString.toList(target: KoreStringList) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val controller = rt.toListControllerHelper()
	target.clear()

	val lenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}tl_len", rt.config.lengthObjective)
	val iCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}tl_i", rt.config.lengthObjective)
	length(lenCursor.holder)
	iCursor.set(fn, 0)

	val stepArgs = rt.argsPath(OopConstants.stringToListStepMacroName)
	fn.data(rt.libStorageArg) {
		modify("$stepArgs.src", name)
		modify("$stepArgs.listPath", target.nbtPath)
	}
	fn.ifScoreCompareRunFunction(iCursor, Relation.LESS_THAN, lenCursor, controller.name)
}
