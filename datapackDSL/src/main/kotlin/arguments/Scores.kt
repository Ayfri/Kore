package arguments

import arguments.numbers.IntRangeOrInt
import arguments.numbers.asRangeOrInt
import arguments.selector.SelectorNbtData
import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

@Serializable(Score.Companion.ScoreSerializer::class)
data class Score(val name: String, val value: IntRangeOrInt) {
	override fun toString() = "$name=$value"

	companion object {
		object ScoreSerializer : ToStringSerializer<Score>()
	}
}

@Serializable(Scores.Companion.ScoresSerializer::class)
data class Scores(val scores: Set<Score> = emptySet()) {
	override fun toString() = scores.joinToString(",", "{", "}") { it.toString() }

	companion object {
		object ScoresSerializer : ToStringSerializer<Scores>()
	}
}

class ScoreBuilder {
	private val scores = mutableSetOf<Score>()

	fun score(name: String, value: IntRangeOrInt) = scores.add(Score(name, value))
	fun score(name: String, value: Int) = scores.add(Score(name, value.asRangeOrInt()))
	fun score(name: String, value: IntRange) = scores.add(Score(name, value.asRangeOrInt()))

	fun build() = Scores(scores)
}

fun SelectorNbtData.scores(block: ScoreBuilder.() -> Unit) {
	val builder = ScoreBuilder()
	builder.block()
	scores = builder.build()
}
