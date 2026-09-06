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
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag

private const val CASE_ARGS_KEY = "${INTERNAL_NAME_PREFIX}case_args"
private const val CASE_CHAR_KEY = "c"
private const val CASE_SCRATCH = "${INTERNAL_NAME_PREFIX}case_scratch"
private const val CAP_REST_SCRATCH = "${INTERNAL_NAME_PREFIX}cap_rest"

/** Macros holder for the per-character table lookup, fed the extracted character as `c`. */
class CaseMapMacros internal constructor() : Macros() {
	val c by "c"
}

/** Shared macros holder for both upper / lower step helpers. */
class CaseStepMacros internal constructor() : Macros() {
	val dst by "dst"
	val i by "i"
	val iPlusOne by "iPlusOne"
	val src by "src"
}

/** Empty macros holder for the upper / lower controllers, whose loop state lives in scores. */
class CaseControllerMacros internal constructor() : Macros()

/**
 * Writes the translation table as a single NBT compound on world load, so a step can map a
 * character with one `set from` instead of scanning 26 `execute if data` branches.
 */
private fun DynamicStringRuntime.registerCaseTable(name: String, domain: CharRange, mapping: (Char) -> Char) {
	val table: NbtTag = nbt { domain.forEach { c -> put(c.toString(), NbtString(mapping(c).toString())) } }
	ensureLoaded(name) {
		data(libStorageArg) {
			modify(tablesPath(name), table)
		}
	}
}

internal fun DynamicStringRuntime.lowerTableHelper() =
	registerCaseTable(OopConstants.stringLowerTableMacroName, 'A'..'Z') { it.lowercaseChar() }

internal fun DynamicStringRuntime.upperTableHelper() =
	registerCaseTable(OopConstants.stringUpperTableMacroName, 'a'..'z') { it.uppercaseChar() }

/**
 * Maps the character sitting in the case args compound through [tableName], leaving it untouched
 * when the table has no entry for it.
 *
 * The character is used as a quoted NBT path key, so it has to be passed as a macro argument of its
 * own: this function is always invoked `with storage <tmpRoot>.<caseArgs>`. A quote or a backslash
 * would break that path, so both are excluded up front; neither has a case, which makes skipping
 * the lookup the right answer anyway.
 */
internal fun DynamicStringRuntime.caseMapHelper(tableName: String): FunctionWithMacros<CaseMapMacros> =
	ensure("${tableName}_map", ::CaseMapMacros) {
		addLine(
			"""execute unless data storage $libStorage ${tmpPath(CASE_ARGS_KEY)}{$CASE_CHAR_KEY:"\""} """ +
				"""unless data storage $libStorage ${tmpPath(CASE_ARGS_KEY)}{$CASE_CHAR_KEY:"\\"} run """ +
				"""data modify storage $libStorage ${caseCharPath()} set from """ +
				"""storage $libStorage ${tablesPath(tableName)}."${'$'}($CASE_CHAR_KEY)""""
		)
	}

/** Extracts `src[i..i+1]`, maps it through [tableName] then appends the result to `dst`. */
private fun DynamicStringRuntime.registerCaseStep(
	stepName: String,
	tableName: String,
): FunctionWithMacros<CaseStepMacros> = ensure(stepName, ::CaseStepMacros) {
	substringHelper()
	val map = caseMapHelper(tableName)

	setSubstringMacro(
		storage = libStorageArg,
		path = caseCharPath(),
		srcStorage = libStorageArg,
		srcPath = heapPath(macros.src),
		startExpr = macros.i,
		endExpr = macros.iPlusOne,
	)
	callMacro(map.name, libStorageArg, tmpPath(CASE_ARGS_KEY))
	data(libStorageArg) {
		modify(heapPath(macros.dst)) {
			append(libStorageArg, caseCharPath(), null, null)
		}
	}
}

internal fun DynamicStringRuntime.lowerStepHelper(): FunctionWithMacros<CaseStepMacros> {
	lowerTableHelper()
	return registerCaseStep(OopConstants.stringLowerStepMacroName, OopConstants.stringLowerTableMacroName)
}

internal fun DynamicStringRuntime.upperStepHelper(): FunctionWithMacros<CaseStepMacros> {
	upperTableHelper()
	return registerCaseStep(OopConstants.stringUpperStepMacroName, OopConstants.stringUpperTableMacroName)
}

/** Path of the single character a case step is currently mapping. */
internal fun DynamicStringRuntime.caseCharPath() = "${tmpPath(CASE_ARGS_KEY)}.$CASE_CHAR_KEY"

private fun DynamicStringRuntime.registerCaseController(
	name: String,
	stepName: String,
): FunctionWithMacros<CaseControllerMacros> = ensure(name, ::CaseControllerMacros) {
	val stepArgs = argsPath(stepName)
	val iCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}case_i", config.lengthObjective)
	val ip1 = ScoreCursor("#${INTERNAL_NAME_PREFIX}case_ip1", config.lengthObjective)
	val len = ScoreCursor("#${INTERNAL_NAME_PREFIX}case_len", config.lengthObjective)

	storeScoreToNbt(iCursor, libStorageArg, "$stepArgs.i")
	scoreOperation(ip1, Operation.SET, iCursor)
	ip1.add(this, 1)
	storeScoreToNbt(ip1, libStorageArg, "$stepArgs.iPlusOne")
	callMacro(stepName, libStorageArg, stepArgs)
	iCursor.add(this, 1)
	ifScoreCompareRunFunction(iCursor, Relation.LESS_THAN, len, name)
}

internal fun DynamicStringRuntime.lowerControllerHelper(): FunctionWithMacros<CaseControllerMacros> {
	lowerStepHelper()
	return registerCaseController(OopConstants.stringLowerMacroName, OopConstants.stringLowerStepMacroName)
}

internal fun DynamicStringRuntime.upperControllerHelper(): FunctionWithMacros<CaseControllerMacros> {
	upperStepHelper()
	return registerCaseController(OopConstants.stringUpperMacroName, OopConstants.stringUpperStepMacroName)
}

context(fn: Function)
private fun DynamicString.runCase(controllerName: String, stepName: String, target: DynamicString): DynamicString {
	val scratch = runtime.scratchString(CASE_SCRATCH)
	scratch.set("")
	val stepArgs = runtime.argsPath(stepName)
	fn.data(runtime.libStorageArg) {
		modify("$stepArgs.src", name)
		modify("$stepArgs.dst", scratch.name)
	}
	val lenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}case_len", runtime.config.lengthObjective)
	val iCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}case_i", runtime.config.lengthObjective)
	length(lenCursor.holder)
	iCursor.set(fn, 0)
	fn.ifScoreCompareRunFunction(iCursor, Relation.LESS_THAN, lenCursor, controllerName)
	if (target != scratch) target.setFrom(scratch)
	return target
}

/**
 * Capitalises only the first character of the string (ASCII).
 *
 * ```
 * "kore lib".capitalize()  // "Kore lib"
 * ```
 */
context(fn: Function)
fun DynamicString.capitalize(target: DynamicString = this) = changeFirstCharCase(target, uppercase = true)

/**
 * Lowercases only the first character of the string (ASCII).
 *
 * ```
 * "Kore Lib".decapitalize()  // "kore Lib"
 * ```
 */
context(fn: Function)
fun DynamicString.decapitalize(target: DynamicString = this) = changeFirstCharCase(target, uppercase = false)

/**
 * Maps a single character through the case table instead of running the whole per-character loop,
 * which would cost a controller call and a length measurement for one character.
 */
context(fn: Function)
private fun DynamicString.changeFirstCharCase(target: DynamicString, uppercase: Boolean): DynamicString {
	val tableName = if (uppercase) OopConstants.stringUpperTableMacroName else OopConstants.stringLowerTableMacroName
	if (uppercase) runtime.upperTableHelper() else runtime.lowerTableHelper()
	val map = runtime.caseMapHelper(tableName)
	val rest = runtime.scratchString(CAP_REST_SCRATCH)

	fn.setSubstring(storage, runtime.caseCharPath(), storage, nbtPath, 0, 1)
	fn.callMacro(map.name, storage, runtime.tmpPath(CASE_ARGS_KEY))
	substringTo(rest, 1)
	fn.copyNbt(storage, target.nbtPath, storage, runtime.caseCharPath())
	target.appendFrom(rest)
	return target
}

/**
 * Converts every ASCII uppercase letter to its lowercase counterpart.
 *
 * ```
 * "KoRe 42".lowercase()  // "kore 42"
 * ```
 */
context(fn: Function)
fun DynamicString.lowercase(target: DynamicString = this): DynamicString {
	runtime.lowerControllerHelper()
	return runCase(OopConstants.stringLowerMacroName, OopConstants.stringLowerStepMacroName, target)
}

/**
 * Converts every ASCII lowercase letter to its uppercase counterpart.
 *
 * ```
 * "KoRe 42".uppercase()  // "KORE 42"
 * ```
 */
context(fn: Function)
fun DynamicString.uppercase(target: DynamicString = this): DynamicString {
	runtime.upperControllerHelper()
	return runCase(OopConstants.stringUpperMacroName, OopConstants.stringUpperStepMacroName, target)
}

/** Expression form of [capitalize]: writes the capitalised value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.capitalized(): DynamicString = capitalize(runtime.tempString())

/** Expression form of [decapitalize]: writes the decapitalised value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.decapitalized(): DynamicString = decapitalize(runtime.tempString())

/** Expression form of [lowercase]: writes the lowercased value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.lowercased(): DynamicString = lowercase(runtime.tempString())

/** Expression form of [uppercase]: writes the uppercased value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.uppercased(): DynamicString = uppercase(runtime.tempString())
