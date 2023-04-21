package serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class EnumOrdinalSerializer<T>(
	private val values: Array<T>,
) : KSerializer<T> where T : Enum<T> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EnumOrdinalSerializer", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeInt()
		return values[value]
	}

	override fun serialize(encoder: Encoder, value: T) {
		encoder.encodeInt(value.ordinal)
	}
}
