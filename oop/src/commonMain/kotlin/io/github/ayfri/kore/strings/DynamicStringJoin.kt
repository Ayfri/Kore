package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue

private const val JOIN_SEPARATOR_KEY = "${INTERNAL_NAME_PREFIX}join_sep"

/** Macros for the `kore_string_join_step` helper. */
class JoinStepMacros internal constructor() : Macros() {
	val dst by "dst"
	val index by "index"
	val listPath by "listPath"
}

/**
 * Step helper appending `list[index]` to the destination string, prefixed by the staged separator
 * for every element but the first, then recursing while elements remain.
 */
internal fun DynamicStringRuntime.joinStepHelper(): FunctionWithMacros<JoinStepMacros> =
	ensure(OopConstants.stringJoinStepMacroName, ::JoinStepMacros) {
		val obj = config.lengthObjective
		val args = argsPath(OopConstants.stringJoinStepMacroName)
		val dst = heapPath(macros.dst)
		val index = ScoreCursor("#${INTERNAL_NAME_PREFIX}join_i", obj)
		val size = ScoreCursor("#${INTERNAL_NAME_PREFIX}join_size", obj)

		addLine(
			"execute if score ${index.holder} $obj matches 1.. run data modify storage $libStorage $dst " +
				"append string storage $libStorage ${tmpPath(JOIN_SEPARATOR_KEY)}"
		)
		addLine(
			"data modify storage $libStorage $dst append string storage $libStorage ${macros.listPath}[${macros.index}]"
		)
		index.add(this, 1)
		storeScoreToNbt(index, libStorageArg, "$args.index")
		ifScoreCompareRunMacro(
			left = index,
			relation = Relation.LESS_THAN,
			right = size,
			name = OopConstants.stringJoinStepMacroName,
			storage = libStorageArg,
			path = args,
		)
	}

/**
 * Concatenates every element of this list into [target], inserting [separator] between them and
 * wrapping the result with [prefix] and [postfix], mirroring [kotlin.collections.joinToString].
 *
 * [target] is overwritten, and an empty list leaves it as `prefix + postfix`.
 *
 * ```
 * ["a", "b"].join(", ", out)                               // out = "a, b"
 * ["a", "b"].join(", ", out, prefix = "[", postfix = "]")  // out = "[a, b]"
 * ```
 */
context(fn: Function)
fun KoreStringList.join(
	separator: String,
	target: DynamicString,
	prefix: String = "",
	postfix: String = "",
) {
	val step = runtime.joinStepHelper()
	val obj = runtime.config.lengthObjective
	val args = runtime.argsPath(OopConstants.stringJoinStepMacroName)

	target.set(prefix)
	fn.data(runtime.libStorageArg) {
		modify(runtime.tmpPath(JOIN_SEPARATOR_KEY), separator)
		modify("$args.dst", target.name)
		modify("$args.listPath", nbtPath)
	}

	val index = ScoreCursor("#${INTERNAL_NAME_PREFIX}join_i", obj)
	val sizeCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}join_size", obj)
	size(sizeCursor.holder)
	index.set(fn, 0)
	index.writeToStorage(fn, runtime.libStorageArg, "$args.index")
	fn.ifScoreCompareRunMacro(
		left = index,
		relation = Relation.LESS_THAN,
		right = sizeCursor,
		name = step.name,
		storage = runtime.libStorageArg,
		path = args,
	)

	if (postfix.isNotEmpty()) target.append(postfix)
}
