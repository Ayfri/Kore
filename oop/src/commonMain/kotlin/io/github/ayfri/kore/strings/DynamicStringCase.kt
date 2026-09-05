package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.function
import io.github.ayfri.kore.commands.scoreboard.Operation
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

private const val CASE_CHAR_PATH = "tmp.kore_string_case_c"
private const val CASE_SCRATCH = "kore_string_case_scratch"
private const val LOWER_TABLE_FN = "kore_string_lower_table"
private const val UPPER_TABLE_FN = "kore_string_upper_table"

/** Shared macros holder for both upper / lower step helpers. */
class CaseStepMacros internal constructor() : Macros() {
	val dst by "dst"
	val i by "i"
	val iPlusOne by "iPlusOne"
	val src by "src"
}

/** Empty holders for upper / lower / table controllers. */
class LowerMacros internal constructor() : Macros()
class LowerStepMacros internal constructor() : Macros()
class UpperMacros internal constructor() : Macros()
class UpperStepMacros internal constructor() : Macros()

private fun DynamicStringRuntime.registerCaseTable(
	name: String,
	domain: CharRange,
	mapping: (Char) -> Char,
) = ensure(name, ::UpperMacros) {
	for (c in domain) {
		val mapped = mapping(c)
		addLine(
			"execute if data storage $libStorage tmp{kore_string_case_c:\"$c\"} run " +
				"data modify storage $libStorage $CASE_CHAR_PATH set value \"$mapped\""
		)
	}
}

internal fun DynamicStringRuntime.lowerTableHelper() =
	registerCaseTable(LOWER_TABLE_FN, 'A'..'Z') { it.lowercaseChar() }

internal fun DynamicStringRuntime.upperTableHelper() =
	registerCaseTable(UPPER_TABLE_FN, 'a'..'z') { it.uppercaseChar() }

private fun DynamicStringRuntime.registerCaseStep(
	stepName: String,
	tableName: String,
): FunctionWithMacros<CaseStepMacros> = ensure(stepName, ::CaseStepMacros) {
	substringHelper()
	val m = macros

	setSubstringMacro(
		storage = libStorageArg,
		path = CASE_CHAR_PATH,
		srcStorage = libStorageArg,
		srcPath = heapPath(m.src),
		startExpr = m.i,
		endExpr = m.iPlusOne,
	)
	function(namespace = datapack.name, name = tableName)
	data(libStorageArg) {
		modify(heapPath(m.dst)) {
			append(libStorageArg, CASE_CHAR_PATH)
		}
	}
}

internal fun DynamicStringRuntime.lowerStepHelper(): FunctionWithMacros<CaseStepMacros> {
	lowerTableHelper()
	return registerCaseStep(OopConstants.stringLowerStepMacroName, LOWER_TABLE_FN)
}

internal fun DynamicStringRuntime.upperStepHelper(): FunctionWithMacros<CaseStepMacros> {
	upperTableHelper()
	return registerCaseStep(OopConstants.stringUpperStepMacroName, UPPER_TABLE_FN)
}

private fun DynamicStringRuntime.registerCaseController(
	name: String,
	stepName: String,
): FunctionWithMacros<UpperMacros> = ensure(name, ::UpperMacros) {
	val stepArgs = argsPath(stepName)
	val iCursor = ScoreCursor("#kore_string_case_i", config.lengthObjective)
	val ip1 = ScoreCursor("#kore_string_case_ip1", config.lengthObjective)
	val len = ScoreCursor("#kore_string_case_len", config.lengthObjective)

	storeScoreToNbt(iCursor, libStorageArg, "$stepArgs.i")
	scoreOperation(ip1, Operation.SET, iCursor)
	ip1.add(this, 1)
	storeScoreToNbt(ip1, libStorageArg, "$stepArgs.iPlusOne")
	callMacro(stepName, libStorageArg, stepArgs)
	iCursor.add(this, 1)
	ifScoreCompareRunFunction(iCursor, Relation.LESS_THAN, len, name)
}

internal fun DynamicStringRuntime.lowerControllerHelper(): FunctionWithMacros<UpperMacros> {
	lowerStepHelper()
	return registerCaseController(OopConstants.stringLowerMacroName, OopConstants.stringLowerStepMacroName)
}

internal fun DynamicStringRuntime.upperControllerHelper(): FunctionWithMacros<UpperMacros> {
	upperStepHelper()
	return registerCaseController(OopConstants.stringUpperMacroName, OopConstants.stringUpperStepMacroName)
}

context(fn: Function)
private fun DynamicString.runCase(controllerName: String, stepName: String, target: DynamicString) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	val scratch = DynamicString(CASE_SCRATCH)
	scratch.set("")
	val stepArgs = rt.argsPath(stepName)
	fn.data(rt.libStorageArg) {
		modify("$stepArgs.src", name)
		modify("$stepArgs.dst", scratch.name)
	}
	val lenCursor = ScoreCursor("#kore_string_case_len", rt.config.lengthObjective)
	val iCursor = ScoreCursor("#kore_string_case_i", rt.config.lengthObjective)
	length(lenCursor.holder)
	iCursor.set(fn, 0)
	fn.ifScoreCompareRunFunction(iCursor, Relation.LESS_THAN, lenCursor, controllerName)
	if (target != scratch) target.setFrom(scratch)
}

/** Converts every ASCII lowercase letter to its uppercase counterpart. */
context(fn: Function)
fun DynamicString.uppercase(target: DynamicString = this) {
	fn.datapack.requireDynamicStringRuntime().upperControllerHelper()
	runCase(OopConstants.stringUpperMacroName, OopConstants.stringUpperStepMacroName, target)
}

/** Converts every ASCII uppercase letter to its lowercase counterpart. */
context(fn: Function)
fun DynamicString.lowercase(target: DynamicString = this) {
	fn.datapack.requireDynamicStringRuntime().lowerControllerHelper()
	runCase(OopConstants.stringLowerMacroName, OopConstants.stringLowerStepMacroName, target)
}

/** Capitalises only the first character of the string (ASCII). */
context(fn: Function)
fun DynamicString.capitalize(target: DynamicString = this) {
	val head = DynamicString("kore_string_cap_head")
	val rest = DynamicString("kore_string_cap_rest")
	substringTo(head, 0, 1)
	substringTo(rest, 1)
	head.uppercase()
	target.setFrom(head)
	target.appendFrom(rest)
}

/** Lowercases only the first character of the string (ASCII). */
context(fn: Function)
fun DynamicString.decapitalize(target: DynamicString = this) {
	val head = DynamicString("kore_string_cap_head")
	val rest = DynamicString("kore_string_cap_rest")
	substringTo(head, 0, 1)
	substringTo(rest, 1)
	head.lowercase()
	target.setFrom(head)
	target.appendFrom(rest)
}
