package io.github.ayfri.kore.strings

import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.scoreboard.copyDataFrom
import io.github.ayfri.kore.scoreboard.copyTo

/** Slot staging the numeric form of a score while it is converted to or from text. */
private const val SCORE_KEY = "${INTERNAL_NAME_PREFIX}score"

/** Slot holding the rendered text of a score before it is glued onto another string. */
private const val SCORE_TEXT = "${INTERNAL_NAME_PREFIX}score_text"

private fun DynamicString.renderScore(fn: Function, score: ScoreboardEntity, target: DynamicString) {
	val path = runtime.tmpPath(SCORE_KEY)
	context(fn) {
		score.copyTo(storage, path, DataType.INT)
		target.setFromNbt(storage, path)
	}
}

/**
 * Appends the decimal representation of [score] to this string, the usual way to build a counter or
 * a leaderboard line without a `score` chat component.
 */
context(fn: Function)
fun DynamicString.appendFrom(score: ScoreboardEntity) {
	val text = runtime.scratchString(SCORE_TEXT)
	renderScore(fn, score, text)
	appendFrom(text)
}

/** Prepends the decimal representation of [score] to this string. */
context(fn: Function)
fun DynamicString.prependFrom(score: ScoreboardEntity) {
	val text = runtime.scratchString(SCORE_TEXT)
	renderScore(fn, score, text)
	prependFrom(text)
}

/**
 * Replaces this string with the decimal representation of [score], so a computed number becomes
 * text every other helper can slice, pad or join.
 *
 * ```kotlin
 * val label = dynamicString("label")
 * label.setFrom(kills)          // "7"
 * label.padStart(3, '0')        // "007"
 * label.prepend("Kills: ")      // "Kills: 007"
 * ```
 */
context(fn: Function)
fun DynamicString.setFrom(score: ScoreboardEntity) = renderScore(fn, score, this)

/**
 * Parses this string as a number and stores it into [target], the inverse of [setFrom]. Anything
 * that is not valid SNBT for a number leaves [target] untouched.
 */
context(fn: Function)
fun DynamicString.toScore(target: ScoreboardEntity) {
	val path = runtime.tmpPath(SCORE_KEY)
	parseTo(storage, path)
	target.copyDataFrom(storage, path)
}
