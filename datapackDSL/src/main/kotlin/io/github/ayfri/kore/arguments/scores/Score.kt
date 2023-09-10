package io.github.ayfri.kore.arguments.scores

import io.github.ayfri.kore.arguments.enums.Relation
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument

sealed interface Score {
	val objective: String

	fun asString(value: IntRangeOrInt): String
}

data class ExecuteScore(val target: ScoreHolderArgument, override val objective: String) : Score {
	override fun asString(value: IntRangeOrInt) = "${target.asString()} $objective matches $value"
	fun asString(other: ExecuteScore, relation: Relation) =
		"${target.asString()} $objective ${relation.asString()} ${other.target.asString()} ${other.objective}"
}

@JvmInline
value class SelectorScore(override val objective: String) : Score {
	override fun asString(value: IntRangeOrInt) = "$objective=$value"
}
