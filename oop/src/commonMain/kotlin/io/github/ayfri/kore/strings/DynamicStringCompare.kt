package io.github.ayfri.kore.strings

import io.github.ayfri.kore.OopConstants
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function

/**
 * Equality result holder.
 *
 * The [holder] score on [objective] contains `0` when the compared values are structurally equal,
 * `1` when they differ. Use [equals] when you want a `0/1` boolean semantics.
 */
data class DynamicStringEquality(val holder: String, val objective: String = OopConstants.stringLengthObjective)

private fun equalityCheckTmpPath() = tmpPath("equality_check")

/**
 * Compares the content of this [DynamicString] with [other] and stores a difference score at the
 * given [diffHolder]:
 * - `0` when the two strings are equal,
 * - `1` when they differ.
 *
 * Usage inside a command: `execute if score diffHolder matches 0 run ...`.
 */
context(fn: Function)
fun DynamicString.equalsTo(other: DynamicString, diffHolder: String = "#kore_string_diff"): DynamicStringEquality {
	val tmp = equalityCheckTmpPath()
	fn.data(storage) {
		modify(tmp) { set(storage, nbtPath) }
	}
	fn.execute {
		storeResult { score(ScoreCursor(diffHolder).asScoreHolder(), OopConstants.stringLengthObjective) }
		run {
			data(storage) {
				modify(tmp) { set(other.storage, other.nbtPath) }
			}
		}
	}
	return DynamicStringEquality(diffHolder)
}

/** Compares this [DynamicString] with a [literal] string. See [equalsTo] for score semantics. */
context(fn: Function)
fun DynamicString.equalsTo(literal: String, diffHolder: String = "#kore_string_diff"): DynamicStringEquality {
	val tmp = equalityCheckTmpPath()
	fn.data(storage) {
		modify(tmp, literal)
	}
	fn.execute {
		storeResult { score(ScoreCursor(diffHolder).asScoreHolder(), OopConstants.stringLengthObjective) }
		run {
			data(storage) {
				modify(tmp) { set(storage, nbtPath) }
			}
		}
	}
	return DynamicStringEquality(diffHolder)
}

/**
 * Stores `1` into [emptyHolder] on the `kore_string_len` objective when this string is empty, `0`
 * otherwise. Implemented by comparing against the empty literal via [equalsTo].
 */
context(fn: Function)
fun DynamicString.isEmpty(emptyHolder: String = "#kore_string_is_empty"): DynamicStringEquality {
	val diff = equalsTo("", emptyHolder)
	return DynamicStringEquality(diff.holder, diff.objective)
}

/**
 * Stores `1` into [prefixHolder] when this string starts with [prefix], `0` otherwise.
 *
 * Uses a scratch heap slot to copy the candidate prefix before comparing it. Supports both literal
 * and dynamic prefixes.
 */
context(fn: Function)
fun DynamicString.startsWith(prefix: String, prefixHolder: String = "#kore_string_starts"): DynamicStringEquality {
	require(prefix.isNotEmpty()) { "startsWith prefix must be non empty (an empty prefix always matches)." }
	val scratch = DynamicString("kore_string_starts_scratch")
	substringTo(scratch, 0, prefix.length)
	val diff = scratch.equalsTo(prefix, prefixHolder)
	return DynamicStringEquality(diff.holder, diff.objective)
}

/**
 * Stores `1` into [prefixHolder] when this string starts with [prefix], `0` otherwise, using a
 * runtime prefix whose length is measured dynamically.
 */
context(fn: Function)
fun DynamicString.startsWith(
	prefix: DynamicString,
	prefixHolder: String = "#kore_string_starts"
): DynamicStringEquality {
	val scratch = DynamicString("kore_string_starts_scratch")
	val prefixLenCursor = ScoreCursor("#kore_string_starts_len")
	val zeroCursor = ScoreCursor("#kore_string_zero")
	zeroCursor.set(fn, 0)
	prefix.length(prefixLenCursor.holder)
	substringDynamicCursors(fn, zeroCursor, prefixLenCursor, scratch)
	return scratch.equalsTo(prefix, prefixHolder)
}

/**
 * Stores `1` into [suffixHolder] when this string ends with [suffix], `0` otherwise.
 */
context(fn: Function)
fun DynamicString.endsWith(suffix: String, suffixHolder: String = "#kore_string_ends"): DynamicStringEquality {
	require(suffix.isNotEmpty()) { "endsWith suffix must be non empty (an empty suffix always matches)." }
	val scratch = DynamicString("kore_string_ends_scratch")
	val lenCursor = ScoreCursor("#kore_string_ends_srclen")
	val startCursor = ScoreCursor("#kore_string_ends_start")
	length(lenCursor.holder)
	startCursor.assignFrom(fn, lenCursor)
	startCursor.sub(fn, suffix.length)
	substringDynamicCursors(fn, startCursor, lenCursor, scratch)
	return scratch.equalsTo(suffix, suffixHolder)
}

/**
 * Dynamic variant of [endsWith] where the suffix length is measured at runtime.
 */
context(fn: Function)
fun DynamicString.endsWith(suffix: DynamicString, suffixHolder: String = "#kore_string_ends"): DynamicStringEquality {
	val scratch = DynamicString("kore_string_ends_scratch")
	val lenCursor = ScoreCursor("#kore_string_ends_srclen")
	val suffixLenCursor = ScoreCursor("#kore_string_ends_suflen")
	val startCursor = ScoreCursor("#kore_string_ends_start")
	length(lenCursor.holder)
	suffix.length(suffixLenCursor.holder)
	startCursor.assignFrom(fn, lenCursor)
	startCursor.subFrom(fn, suffixLenCursor)
	substringDynamicCursors(fn, startCursor, lenCursor, scratch)
	return scratch.equalsTo(suffix, suffixHolder)
}
