package io.github.ayfri.kore.strings

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.commands.DataModifyOperation
import io.github.ayfri.kore.commands.data
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity

private const val PAD_CAP_SCORE = "#${INTERNAL_NAME_PREFIX}pad_cap"
private const val PAD_CHAR_SCRATCH = "${INTERNAL_NAME_PREFIX}pad_char"
private const val PAD_DIFF_SCORE = "#${INTERNAL_NAME_PREFIX}pad_diff"
private const val PAD_LEN_SCORE = "#${INTERNAL_NAME_PREFIX}pad_len"
private const val PAD_SCRATCH = "${INTERNAL_NAME_PREFIX}pad_scratch"
private const val PAD_SRC_COPY = "${INTERNAL_NAME_PREFIX}pad_src"

/** Target width of a padding operation: either known at generation time, or read from a score. */
private sealed interface PadLength {
	data class Dynamic(val score: ScoreboardEntity) : PadLength
	data class Static(val value: Int) : PadLength
}

private fun applyPad(
	fn: Function,
	source: DynamicString,
	length: PadLength,
	padChar: Char,
	target: DynamicString,
	prepend: Boolean,
): DynamicString {
	if (length is PadLength.Static) require(length.value >= 0) { "target length must be non negative, got ${length.value}" }
	val rt = fn.datapack.requireDynamicStringRuntime()
	val obj = rt.config.lengthObjective

	val srcCopy = rt.scratchString(PAD_SRC_COPY)
	context(fn) { srcCopy.setFrom(source) }

	val curLen = ScoreCursor(PAD_LEN_SCORE, obj)
	context(fn) { srcCopy.length(curLen.holder) }
	val diff = ScoreCursor(PAD_DIFF_SCORE, obj)
	when (length) {
		is PadLength.Dynamic -> diff.assignFrom(fn, length.score)
		is PadLength.Static -> diff.set(fn, length.value)
	}
	diff.subFrom(fn, curLen)

	context(fn) { target.setFrom(srcCopy) }
	if (length is PadLength.Static && length.value == 0) return target

	val padUnit = rt.scratchString(PAD_CHAR_SCRATCH)
	val scratch = rt.scratchString(PAD_SCRATCH)
	fn.data(rt.libStorageArg) {
		modify(padUnit.nbtPath, padChar.toString())
		modify(scratch.nbtPath, padChar.toString())
	}

	val capCursor = ScoreCursor(PAD_CAP_SCORE, obj)
	capCursor.assignFrom(fn, diff)
	capCursor.sub(fn, 1)
	repeatInto(fn, scratch, padUnit, capCursor)

	val glue: DataModifyOperation.() -> List<Argument> =
		if (prepend) {
			{ prepend(rt.libStorageArg, scratch.nbtPath, null, null) }
		} else {
			{ append(rt.libStorageArg, scratch.nbtPath, null, null) }
		}

	fn.padGlue(diff, obj, rt.libStorageArg, target.nbtPath, glue)
	return target
}

/** Appends or prepends the built padding only when the string was actually shorter than requested. */
private fun Function.padGlue(
	diff: ScoreCursor,
	obj: String,
	storage: StorageArgument,
	path: String,
	glue: DataModifyOperation.() -> List<Argument>,
) = execute {
	ifCondition { score(diff.asScoreHolder(), obj, rangeOrIntStart(1)) }
	run { data(storage) { modify(path, glue) } }
}

/**
 * Kotlin-style `padStart`: if the current length is strictly smaller than [targetLength], prepends
 * enough copies of [padChar] so the total reaches [targetLength]. Otherwise [target] receives a
 * copy of this string unchanged.
 *
 * ```
 * "42".padStart(5, '0')    // "00042"
 * "hello".padStart(3)      // "hello", already long enough
 * ```
 */
context(fn: Function)
fun DynamicString.padStart(targetLength: Int, padChar: Char = ' ', target: DynamicString = this) =
	applyPad(fn, this, PadLength.Static(targetLength), padChar, target, prepend = true)

/**
 * Pads to a width only known at runtime, so a column can be aligned on the longest entry of a
 * scoreboard, a list or any other measured value.
 *
 * ```
 * // width holds 5 at runtime
 * "42".padStart(width, '0')  // "00042"
 * ```
 */
context(fn: Function)
fun DynamicString.padStart(targetLength: ScoreboardEntity, padChar: Char = ' ', target: DynamicString = this) =
	applyPad(fn, this, PadLength.Dynamic(targetLength), padChar, target, prepend = true)

/**
 * Kotlin-style `padEnd`: if the current length is strictly smaller than [targetLength], appends
 * enough copies of [padChar] so the total reaches [targetLength].
 *
 * ```
 * "42".padEnd(5, '.')  // "42..."
 * ```
 */
context(fn: Function)
fun DynamicString.padEnd(targetLength: Int, padChar: Char = ' ', target: DynamicString = this) =
	applyPad(fn, this, PadLength.Static(targetLength), padChar, target, prepend = false)

/**
 * Runtime-width variant of [padEnd].
 *
 * ```
 * // width holds 5 at runtime
 * "42".padEnd(width)  // "42   "
 * ```
 */
context(fn: Function)
fun DynamicString.padEnd(targetLength: ScoreboardEntity, padChar: Char = ' ', target: DynamicString = this) =
	applyPad(fn, this, PadLength.Dynamic(targetLength), padChar, target, prepend = false)

/** Expression form of [padStart]: writes the padded value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.paddedStart(targetLength: Int, padChar: Char = ' '): DynamicString =
	padStart(targetLength, padChar, runtime.tempString())

/** Expression form of [padStart] with a runtime width. See [paddedStart]. */
context(fn: Function)
fun DynamicString.paddedStart(targetLength: ScoreboardEntity, padChar: Char = ' '): DynamicString =
	padStart(targetLength, padChar, runtime.tempString())

/** Expression form of [padEnd]: writes the padded value into a fresh anonymous slot. */
context(fn: Function)
fun DynamicString.paddedEnd(targetLength: Int, padChar: Char = ' '): DynamicString =
	padEnd(targetLength, padChar, runtime.tempString())

/** Expression form of [padEnd] with a runtime width. See [paddedEnd]. */
context(fn: Function)
fun DynamicString.paddedEnd(targetLength: ScoreboardEntity, padChar: Char = ' '): DynamicString =
	padEnd(targetLength, padChar, runtime.tempString())
