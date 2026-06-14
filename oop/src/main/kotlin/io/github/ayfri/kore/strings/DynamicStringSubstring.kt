package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.FunctionWithMacros
import io.github.ayfri.kore.functions.Macros
import io.github.ayfri.kore.functions.getValue
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.scoreboard.copyTo

/** Macro identifiers for the shared `kore_string_substring` helper. */
class SubstringMacros internal constructor() : Macros() {
	val src by "src"
	val dst by "dst"
	val start by "start"
	val end by "end"
}

internal fun DynamicStringRuntime.substringHelper(): FunctionWithMacros<SubstringMacros> =
	ensure(OopConstants.stringSubstringMacroName, ::SubstringMacros) {
		setSubstringMacro(
			storage = libStorageArg,
			path = heapPath(macros.dst),
			srcStorage = libStorageArg,
			srcPath = heapPath(macros.src),
			startExpr = macros.start,
			endExpr = macros.end,
		)
	}

/** Handle to the substring helper function, materialising its generation the first time it is read. */
val DynamicStringRuntime.substring: FunctionWithMacros<SubstringMacros> get() = substringHelper()

/**
 * In-place substring based on statically known indices.
 *
 * Mirrors the [kotlin.String.substring] semantics: [start] is inclusive, [end] is exclusive. When
 * [end] is `null`, the slice extends to the end of the underlying string.
 */
context(fn: Function)
fun DynamicString.substring(start: Int, end: Int? = null) = fn.data(storage) {
	modify(nbtPath) {
		if (end == null) set(storage, nbtPath, start)
		else set(storage, nbtPath, start, end)
	}
}

/** Writes a static substring of this string into [target]. */
context(fn: Function)
fun DynamicString.substringTo(target: DynamicString, start: Int, end: Int? = null) = fn.data(target.storage) {
	modify(target.nbtPath) {
		if (end == null) set(storage, nbtPath, start)
		else set(storage, nbtPath, start, end)
	}
}

private fun DynamicString.invokeSubstringMacro(
	fn: Function,
	target: DynamicString,
	writeStart: (StorageArgument, String) -> Unit,
	writeEnd: (StorageArgument, String) -> Unit,
) {
	val rt = fn.datapack.requireDynamicStringRuntime()
	rt.substringHelper()
	val args = rt.argsPath(OopConstants.stringSubstringMacroName)
	fn.data(rt.libStorageArg) {
		modify("$args.src", name)
		modify("$args.dst", target.name)
	}
	writeStart(rt.libStorageArg, "$args.start")
	writeEnd(rt.libStorageArg, "$args.end")
	fn.callMacro(OopConstants.stringSubstringMacroName, rt.libStorageArg, args)
}

/**
 * Dynamic substring variant accepting runtime indices backed by scoreboard scores.
 *
 * The indices are serialised into `kore_string_lib:memory args.kore_string_substring.*` then the
 * hidden macro function is invoked to perform the actual slicing on [target] (defaults to `this`).
 */
context(fn: Function)
fun DynamicString.substringDynamic(
	start: ScoreboardEntity,
	end: ScoreboardEntity,
	target: DynamicString = this,
) = invokeSubstringMacro(
	fn = fn,
	target = target,
	writeStart = { store, path -> start.copyTo(store, path, DataType.INT) },
	writeEnd = { store, path -> end.copyTo(store, path, DataType.INT) },
)

/** Internal dynamic substring variant using raw [ScoreCursor]s for the bounds. */
internal fun DynamicString.substringDynamicCursors(
	fn: Function,
	start: ScoreCursor,
	end: ScoreCursor,
	target: DynamicString = this,
) = invokeSubstringMacro(
	fn = fn,
	target = target,
	writeStart = { store, path -> start.writeToStorage(fn, store, path, DataType.INT) },
	writeEnd = { store, path -> end.writeToStorage(fn, store, path, DataType.INT) },
)

/** Returns the character located at [index] into [target] using a static index. */
context(fn: Function)
fun DynamicString.charAt(index: Int, target: DynamicString = this) = substringTo(target, index, index + 1)

/** Copies the character located at the runtime [index] into [target]. */
context(fn: Function)
fun DynamicString.charAt(index: ScoreboardEntity, target: DynamicString = this) {
	val endCursor = ScoreCursor("#kore_string_char_end")
	val indexCursor = ScoreCursor("#kore_string_char_start")
	fn.execute {
		storeResult { score(indexCursor.asScoreHolder(), indexCursor.objective) }
		run {
			scoreboard {
				players {
					get(index.entity.asSelector(), index.name)
				}
			}
		}
	}
	endCursor.assignFrom(fn, indexCursor)
	endCursor.add(fn, 1)
	substringDynamicCursors(fn, indexCursor, endCursor, target)
}

/** Static `take(n)` alias: writes the first [n] characters into [target] (defaults to `this`). */
context(fn: Function)
fun DynamicString.take(n: Int, target: DynamicString = this) = substringTo(target, 0, n)

/** Static `drop(n)` alias: writes everything from index [n] onward into [target]. */
context(fn: Function)
fun DynamicString.drop(n: Int, target: DynamicString = this) = substringTo(target, n)

/**
 * Static `takeLast(n)`: writes the last [n] characters into [target].
 * Requires [n] to be non negative.
 *
 * Implemented by first measuring the length at runtime then dispatching to [substringDynamic].
 */
context(fn: Function)
fun DynamicString.takeLast(n: Int, target: DynamicString = this) {
	require(n >= 0) { "takeLast n must be non negative, got $n" }
	val lenCursor = ScoreCursor("#kore_string_tl_len")
	val startCursor = ScoreCursor("#kore_string_tl_start")
	length(lenCursor.holder)
	startCursor.assignFrom(fn, lenCursor)
	startCursor.sub(fn, n)
	substringDynamicCursors(fn, startCursor, lenCursor, target)
}

/**
 * Static `dropLast(n)`: writes everything but the last [n] characters into [target].
 */
context(fn: Function)
fun DynamicString.dropLast(n: Int, target: DynamicString = this) {
	require(n >= 0) { "dropLast n must be non negative, got $n" }
	val lenCursor = ScoreCursor("#kore_string_dl_len")
	val endCursor = ScoreCursor("#kore_string_dl_end")
	val zeroCursor = ScoreCursor("#kore_string_zero")
	length(lenCursor.holder)
	endCursor.assignFrom(fn, lenCursor)
	endCursor.sub(fn, n)
	zeroCursor.set(fn, 0)
	substringDynamicCursors(fn, zeroCursor, endCursor, target)
}
