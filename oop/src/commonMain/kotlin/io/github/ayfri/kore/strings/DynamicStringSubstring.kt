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
	val dst by "dst"
	val end by "end"
	val src by "src"
	val start by "start"
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

private fun DynamicString.invokeSubstringMacro(
	fn: Function,
	target: DynamicString,
	writeStart: (StorageArgument, String) -> Unit,
	writeEnd: (StorageArgument, String) -> Unit,
) {
	runtime.substringHelper()
	val args = runtime.argsPath(OopConstants.stringSubstringMacroName)
	fn.data(runtime.libStorageArg) {
		modify("$args.src", name)
		modify("$args.dst", target.name)
	}
	writeStart(runtime.libStorageArg, "$args.start")
	writeEnd(runtime.libStorageArg, "$args.end")
	fn.callMacro(OopConstants.stringSubstringMacroName, runtime.libStorageArg, args)
}

/** Returns the character located at [index] into [target] using a static index. */
context(fn: Function)
fun DynamicString.charAt(index: Int, target: DynamicString = this) = substringTo(target, index, index + 1)

/** Copies the character located at the runtime [index] into [target]. */
context(fn: Function)
fun DynamicString.charAt(index: ScoreboardEntity, target: DynamicString = this) {
	val obj = runtime.config.lengthObjective
	val endCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}char_end", obj)
	val indexCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}char_start", obj)
	fn.execute {
		storeResult { score(indexCursor.asScoreHolder(), indexCursor.objective) }
		run {
			scoreboard {
				players {
					get(index.entity.asScoreHolder(), index.name)
				}
			}
		}
	}
	endCursor.assignFrom(fn, indexCursor)
	endCursor.add(fn, 1)
	substringDynamicCursors(fn, indexCursor, endCursor, target)
}

/** Static `drop(n)` alias: writes everything from index [n] onward into [target]. */
context(fn: Function)
fun DynamicString.drop(n: Int, target: DynamicString = this) = substringTo(target, n)

/** Static `dropLast(n)`: writes everything but the last [n] characters into [target]. */
context(fn: Function)
fun DynamicString.dropLast(n: Int, target: DynamicString = this) {
	require(n >= 0) { "dropLast n must be non negative, got $n" }
	val obj = runtime.config.lengthObjective
	val endCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}droplast_end", obj)
	val lenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}droplast_len", obj)
	val zeroCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}zero", obj)
	length(lenCursor.holder)
	endCursor.assignFrom(fn, lenCursor)
	endCursor.sub(fn, n)
	zeroCursor.set(fn, 0)
	substringDynamicCursors(fn, zeroCursor, endCursor, target)
}

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

/**
 * Dynamic substring variant accepting runtime indices backed by scoreboard scores.
 *
 * The indices are serialised into the substring helper's macro args then the hidden macro function
 * is invoked to perform the actual slicing on [target] (defaults to `this`).
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

/** Writes a static substring of this string into [target]. */
context(fn: Function)
fun DynamicString.substringTo(target: DynamicString, start: Int, end: Int? = null) = fn.data(target.storage) {
	modify(target.nbtPath) {
		if (end == null) set(storage, nbtPath, start)
		else set(storage, nbtPath, start, end)
	}
}

/** Static `take(n)` alias: writes the first [n] characters into [target] (defaults to `this`). */
context(fn: Function)
fun DynamicString.take(n: Int, target: DynamicString = this) = substringTo(target, 0, n)

/**
 * Static `takeLast(n)`: writes the last [n] characters into [target]. Requires [n] to be non
 * negative.
 *
 * Implemented by first measuring the length at runtime then dispatching to the substring macro.
 */
context(fn: Function)
fun DynamicString.takeLast(n: Int, target: DynamicString = this) {
	require(n >= 0) { "takeLast n must be non negative, got $n" }
	val obj = runtime.config.lengthObjective
	val lenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}takelast_len", obj)
	val startCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}takelast_start", obj)
	length(lenCursor.holder)
	startCursor.assignFrom(fn, lenCursor)
	startCursor.sub(fn, n)
	substringDynamicCursors(fn, startCursor, lenCursor, target)
}
