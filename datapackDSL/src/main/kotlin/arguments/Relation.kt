package arguments

import arguments.numbers.IntRangeOrInt
import arguments.numbers.rangeOrInt
import arguments.numbers.rangeOrIntEnd
import arguments.numbers.rangeOrIntStart
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

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
		val values = values()

		object RelationSerializer : KSerializer<Relation> by LowercaseSerializer(values) {
			override fun serialize(encoder: Encoder, value: Relation) = encoder.encodeString(value.symbol)
		}
	}
}
