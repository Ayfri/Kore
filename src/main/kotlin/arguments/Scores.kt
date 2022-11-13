package arguments

import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

@Serializable(Score.Companion.ScoreSerializer::class)
data class Score(val name: String, val value: RangeOrInt)  {
	override fun toString() = "$name=${value}"
	
	companion object {
		object ScoreSerializer : ToStringSerializer<Score>()
	}
}

@Serializable(Scores.Companion.ScoresSerializer::class)
data class Scores(val scores: Map<String, Score> = emptyMap())  {
	override fun toString() = scores.entries.joinToString(",", "{", "}") { "${it.key}=${it.value}" }
	
	companion object {
		object ScoresSerializer : ToStringSerializer<Scores>()
	}
}

class ScoreBuilder {
	private val scores = mutableMapOf<String, Score>()
	
	fun score(name: String, value: RangeOrInt) = scores.put(name, Score(name, value))
	fun score(name: String, value: Int) = scores.put(name, Score(name, value.asRangeOrInt()))
	fun score(name: String, value: IntRange) = scores.put(name, Score(name, value.asRangeOrInt()))
	
	fun build() = Scores(scores)
}

fun SelectorNbtData.scores(block: ScoreBuilder.() -> Unit) {
	val builder = ScoreBuilder()
	builder.block()
	scores = builder.build()
}
