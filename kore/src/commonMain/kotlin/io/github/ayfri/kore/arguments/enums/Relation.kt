package io.github.ayfri.kore.arguments.enums

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntEnd
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrIntStart
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable(Relation.Companion.RelationSerializer::class)
enum class Relation(val symbol: String) : Argument {
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL_TO("<="),
	EQUAL_TO("="),
	GREATER_THAN_OR_EQUAL_TO(">="),
	GREATER_THAN(">");

	override fun asString() = symbol

	fun applyAsRange(value: Int): IntRangeOrInt = when (this) {
		LESS_THAN -> rangeOrIntEnd(value - 1)
		LESS_THAN_OR_EQUAL_TO -> rangeOrIntEnd(value)
		EQUAL_TO -> rangeOrInt(value)
		GREATER_THAN_OR_EQUAL_TO -> rangeOrIntStart(value)
		GREATER_THAN -> rangeOrIntStart(value + 1)
	}

	companion object {
		data object RelationSerializer : KSerializer<Relation> by LowercaseSerializer(entries, { symbol })
	}
}
