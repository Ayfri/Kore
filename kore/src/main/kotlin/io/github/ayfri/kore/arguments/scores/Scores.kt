package io.github.ayfri.kore.arguments.scores

import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.numbers.ranges.*
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import kotlin.ranges.IntRange as KotlinIntRange

open class Scores<T : Score>(val scores: MutableSet<String> = mutableSetOf()) {
	internal open fun addScore(string: String) {
		scores.add(string)
	}

	infix fun <T : Score> T.lessThan(value: Int) = addScore(asString(rangeOrIntEnd(value - 1)))
	infix fun ExecuteScore.lessThan(score: ExecuteScore) = addScore(asString(score, Relation.LESS_THAN))

	infix fun <T : Score> T.lessThanOrEqualTo(value: Int) = addScore(asString(rangeOrIntEnd(value)))
	infix fun ExecuteScore.lessThanOrEqualTo(score: ExecuteScore) = addScore(asString(score, Relation.LESS_THAN_OR_EQUAL_TO))

	infix fun <T : Score> T.equalTo(value: Int) = addScore(asString(rangeOrInt(value)))
	infix fun ExecuteScore.equalTo(score: ExecuteScore) = addScore(asString(score, Relation.EQUAL_TO))

	infix fun <T : Score> T.greaterThanOrEqualTo(value: Int) = addScore(asString(rangeOrIntStart(value)))
	infix fun ExecuteScore.greaterThanOrEqualTo(score: ExecuteScore) = addScore(asString(score, Relation.GREATER_THAN_OR_EQUAL_TO))

	infix fun <T : Score> T.greaterThan(value: Int) = addScore(asString(rangeOrIntStart(value + 1)))
	infix fun ExecuteScore.greaterThan(score: ExecuteScore) = addScore(asString(score, Relation.GREATER_THAN))

	infix fun <T : Score> T.matches(value: IntRangeOrInt) = addScore(asString(value))
	infix fun <T : Score> T.matches(value: KotlinIntRange) = addScore(asString(value.asRangeOrInt()))
}

fun Scores<SelectorScore>.score(name: String) = SelectorScore(name)
fun Scores<SelectorScore>.score(name: String, value: Int) = addScore("$name=$value")
fun Scores<SelectorScore>.score(name: String, value: IntRangeOrInt) = addScore("$name=$value")
fun Scores<SelectorScore>.score(name: String, value: KotlinIntRange) = addScore("$name=$value")
fun Scores<SelectorScore>.score(name: String, value: Int, relation: Relation) =
	addScore(SelectorScore(name).asString(relation.applyAsRange(value)))

fun Scores<ExecuteScore>.score(holder: ScoreHolderArgument, name: String) = ExecuteScore(holder, name)
fun Scores<ExecuteScore>.score(holder: ScoreHolderArgument, name: String, value: Int, relation: Relation) =
	addScore(ExecuteScore(holder, name).asString(relation.applyAsRange(value)))

context(Scores<SelectorScore>)
infix fun String.lessThan(value: Int) = addScore("$this=${rangeOrIntEnd(value - 1)}")

context(Scores<SelectorScore>)
infix fun String.lessThanOrEqualTo(value: Int) = addScore("$this=${rangeOrIntEnd(value)}")

context(Scores<SelectorScore>)
infix fun String.equalTo(value: Int) = addScore("$this=${rangeOrInt(value)}")

context(Scores<SelectorScore>)
infix fun String.greaterThanOrEqualTo(value: Int) = addScore("$this=${rangeOrIntStart(value)}")

context(Scores<SelectorScore>)
infix fun String.greaterThan(value: Int) = addScore("$this=${rangeOrIntStart(value + 1)}")

context(Scores<SelectorScore>)
infix fun String.matches(value: Int) = addScore("$this=$value")
