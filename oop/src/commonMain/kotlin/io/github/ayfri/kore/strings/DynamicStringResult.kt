package io.github.ayfri.kore.strings

import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.commands.execute.ExecuteCondition
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.entities.fakePlayer
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity

/**
 * Score-backed result of a string operation: an index for [DynamicString.indexOf], a count for
 * [DynamicString.count], a length for [DynamicString.length], `0`/`1` for every predicate.
 *
 * It is a [ScoreboardEntity], so a result feeds straight back into any helper taking a runtime score
 * ([DynamicString.repeat], [DynamicString.padStart], [DynamicString.get], [DynamicString.setFrom])
 * and into the whole Kore scoreboard DSL.
 *
 * ```
 * val name = dynamicString("name")
 * name.padEnd(name.length())        // no-op width, but any result works as a width
 * (name eq "kore").then { say("hi") }
 * ```
 */
class DynamicStringResult(val holder: String, val objective: String) : ScoreboardEntity(objective, fakePlayer(holder)) {
	override fun equals(other: Any?) = other is DynamicStringResult && other.holder == holder && other.objective == objective
	override fun hashCode() = 31 * holder.hashCode() + objective.hashCode()
	override fun toString() = "$holder $objective"
}

/** Matches this result inside an `execute if`/`unless` block, defaulting to the `1 == true` convention. */
fun ExecuteCondition.result(result: DynamicStringResult, range: IntRangeOrInt = rangeOrInt(1)) =
	score(result.entity.asScoreHolder(), result.objective, range)

/**
 * Runs [block] when the result falls inside [range].
 *
 * ```
 * text.count(",").matching(rangeOrIntStart(3)) { say("too many commas") }
 * ```
 */
context(fn: Function)
fun DynamicStringResult.matching(range: IntRangeOrInt, block: Function.() -> Unit) = fn.execute {
	ifCondition { result(this@matching, range) }
	run(block)
}

/**
 * Runs [block] when the predicate held, the `1` branch.
 *
 * ```
 * (name startsWith "kore_").then { say("internal name") }
 * ```
 */
context(fn: Function)
fun DynamicStringResult.then(block: Function.() -> Unit) = matching(rangeOrInt(1), block)

/**
 * Runs [block] when the predicate did not hold, the `0` branch.
 *
 * ```
 * (name eq "kore").otherwise { say("not kore") }
 * ```
 */
context(fn: Function)
fun DynamicStringResult.otherwise(block: Function.() -> Unit) = matching(rangeOrInt(0), block)
