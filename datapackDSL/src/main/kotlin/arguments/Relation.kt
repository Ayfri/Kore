package arguments

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

	companion object {
		val values = values()

		object RelationSerializer : KSerializer<Relation> by LowercaseSerializer(values) {
			override fun serialize(encoder: Encoder, value: Relation) = encoder.encodeString(value.symbol)
		}
	}
}
