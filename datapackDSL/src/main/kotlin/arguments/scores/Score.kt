package arguments.scores

import arguments.Argument
import arguments.Relation
import arguments.numbers.IntRangeOrInt

sealed interface Score {
	val objective: String

	fun asString(value: IntRangeOrInt): String
}

data class ExecuteScore(val target: Argument.ScoreHolder, override val objective: String) : Score {
	override fun asString(value: IntRangeOrInt) = "${target.asString()} $objective matches $value"
	fun asString(other: ExecuteScore, relation: Relation) =
		"${target.asString()} $objective ${relation.asString()} ${other.target.asString()} ${other.objective}"
}

@JvmInline
value class SelectorScore(override val objective: String) : Score {
	override fun asString(value: IntRangeOrInt) = "$objective=$value"
}
