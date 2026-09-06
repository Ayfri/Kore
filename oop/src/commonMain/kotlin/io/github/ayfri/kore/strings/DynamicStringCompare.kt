package io.github.ayfri.kore.strings

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.functions.Function

private const val COMPARE_TMP_KEY = "${INTERNAL_NAME_PREFIX}equality_check"
private const val ENDS_SCRATCH = "${INTERNAL_NAME_PREFIX}ends_scratch"
private const val STARTS_SCRATCH = "${INTERNAL_NAME_PREFIX}starts_scratch"

/** Default score holder storing the result of an [endsWith] check. */
const val ENDS_WITH_RESULT_HOLDER = "#kore_string_ends"

/** Default score holder storing the result of an [equalsTo] check. */
const val EQUALS_RESULT_HOLDER = "#kore_string_equals"

/** Default score holder storing the result of an [isEmpty] check. */
const val IS_EMPTY_RESULT_HOLDER = "#kore_string_is_empty"

/** Default score holder storing the result of a [startsWith] check. */
const val STARTS_WITH_RESULT_HOLDER = "#kore_string_starts"

/**
 * Compares this string with [other] and stores `1` into [resultHolder] when both are equal, `0`
 * otherwise.
 *
 * `data modify` reports a failure when it would not change anything, so copying [other] over a
 * scratch slot already holding this string succeeds only when they differ; the result is negated
 * to expose the usual `1 == true` convention.
 */
context(fn: Function)
fun DynamicString.equalsTo(
	other: DynamicString,
	resultHolder: String = EQUALS_RESULT_HOLDER,
): DynamicStringResult {
	val tmp = runtime.tmpPath(COMPARE_TMP_KEY)
	fn.data(storage) {
		modify(tmp) { set(storage, nbtPath) }
	}
	return fn.storeInvertedDiff(runtime, resultHolder) {
		data(storage) {
			modify(tmp) { set(other.storage, other.nbtPath) }
		}
	}
}

/**
 * Compares this string with a [literal] value. See [equalsTo] for score semantics.
 *
 * ```
 * "kore".equalsTo("kore")  // 1
 * "kore".equalsTo("lib")   // 0
 * ```
 */
context(fn: Function)
fun DynamicString.equalsTo(literal: String, resultHolder: String = EQUALS_RESULT_HOLDER): DynamicStringResult {
	val tmp = runtime.tmpPath(COMPARE_TMP_KEY)
	fn.data(storage) {
		modify(tmp, literal)
	}
	return fn.storeInvertedDiff(runtime, resultHolder) {
		data(storage) {
			modify(tmp) { set(storage, nbtPath) }
		}
	}
}

/**
 * Checks whether this string ends with [suffix].
 *
 * ```
 * (path endsWith ".json").then { say("json file") }
 * ```
 */
context(fn: Function)
infix fun DynamicString.endsWith(suffix: String): DynamicStringResult {
	val resultHolder = ENDS_WITH_RESULT_HOLDER
	require(suffix.isNotEmpty()) { "endsWith suffix must be non empty (an empty suffix always matches)." }
	val obj = runtime.config.lengthObjective
	val scratch = runtime.scratchString(ENDS_SCRATCH)
	val lenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}ends_srclen", obj)
	val startCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}ends_start", obj)
	length(lenCursor.holder)
	startCursor.assignFrom(fn, lenCursor)
	startCursor.sub(fn, suffix.length)
	substringDynamicCursors(fn, startCursor, lenCursor, scratch)
	return scratch.equalsTo(suffix, resultHolder)
}

/** Dynamic variant of [endsWith] where the suffix length is measured at runtime. */
context(fn: Function)
infix fun DynamicString.endsWith(suffix: DynamicString): DynamicStringResult {
	val resultHolder = ENDS_WITH_RESULT_HOLDER
	val obj = runtime.config.lengthObjective
	val scratch = runtime.scratchString(ENDS_SCRATCH)
	val lenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}ends_srclen", obj)
	val startCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}ends_start", obj)
	val suffixLenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}ends_suflen", obj)
	length(lenCursor.holder)
	suffix.length(suffixLenCursor.holder)
	startCursor.assignFrom(fn, lenCursor)
	startCursor.subFrom(fn, suffixLenCursor)
	substringDynamicCursors(fn, startCursor, lenCursor, scratch)
	return scratch.equalsTo(suffix, resultHolder)
}

/**
 * Checks whether this string is empty.
 *
 * ```
 * "".isEmpty()      // 1
 * "kore".isEmpty()  // 0
 * ```
 */
context(fn: Function)
fun DynamicString.isEmpty() = equalsTo("", IS_EMPTY_RESULT_HOLDER)

/**
 * Checks whether this string starts with [prefix], copying the candidate prefix into a scratch slot
 * before comparing it.
 *
 * ```
 * (name startsWith "kore_").then { say("internal name") }
 * ```
 */
context(fn: Function)
infix fun DynamicString.startsWith(prefix: String): DynamicStringResult {
	val resultHolder = STARTS_WITH_RESULT_HOLDER
	require(prefix.isNotEmpty()) { "startsWith prefix must be non empty (an empty prefix always matches)." }
	val scratch = runtime.scratchString(STARTS_SCRATCH)
	substringTo(scratch, 0, prefix.length)
	return scratch.equalsTo(prefix, resultHolder)
}

/** Dynamic variant of [startsWith] where the prefix length is measured at runtime. */
context(fn: Function)
infix fun DynamicString.startsWith(prefix: DynamicString): DynamicStringResult {
	val resultHolder = STARTS_WITH_RESULT_HOLDER
	val obj = runtime.config.lengthObjective
	val prefixLenCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}starts_len", obj)
	val scratch = runtime.scratchString(STARTS_SCRATCH)
	val zeroCursor = ScoreCursor("#${INTERNAL_NAME_PREFIX}zero", obj)
	zeroCursor.set(fn, 0)
	prefix.length(prefixLenCursor.holder)
	substringDynamicCursors(fn, zeroCursor, prefixLenCursor, scratch)
	return scratch.equalsTo(prefix, resultHolder)
}

/**
 * Runs [diffCommand] under `execute store success`, which yields `1` when the values differed, then
 * flips it so [resultHolder] ends up holding the `1 == equal` convention shared by every predicate.
 */
private fun Function.storeInvertedDiff(
	runtime: DynamicStringRuntime,
	resultHolder: String,
	diffCommand: Function.() -> Unit,
): DynamicStringResult {
	val obj = runtime.config.lengthObjective
	val diff = ScoreCursor("#${INTERNAL_NAME_PREFIX}diff", obj)
	val result = ScoreCursor(resultHolder, obj)
	execute {
		storeSuccess { score(diff.asScoreHolder(), obj) }
		run { diffCommand() }
	}
	result.set(this, 0)
	execute {
		ifCondition { score(diff.asScoreHolder(), obj, rangeOrInt(0)) }
		run { scoreboard { players { set(result.asScoreHolder(), obj, 1) } } }
	}
	return DynamicStringResult(resultHolder, obj)
}

/**
 * Infix alias of [equalsTo] against a literal, the readable form when the result feeds a branch.
 *
 * ```
 * (name eq "kore").then { say("exact match") }
 * ```
 */
context(fn: Function)
infix fun DynamicString.eq(value: String) = equalsTo(value)

/** Infix alias of [equalsTo] against another dynamic string. See [eq]. */
context(fn: Function)
infix fun DynamicString.eq(other: DynamicString) = equalsTo(other)
