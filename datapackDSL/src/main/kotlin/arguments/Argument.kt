package arguments

import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.ToStringSerializer

@Serializable(Argument.ArgumentSerializer::class)
interface Argument {
	fun asString(): String

	object ArgumentSerializer : ToStringSerializer<Argument>() {
		override fun serialize(encoder: Encoder, value: Argument) = encoder.encodeString(value.asString())
	}
}
