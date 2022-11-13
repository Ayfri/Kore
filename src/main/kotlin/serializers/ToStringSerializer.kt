package serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class ToStringSerializer<T> : KSerializer<T> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ToStringSerializer", PrimitiveKind.STRING)
	
	override fun deserialize(decoder: Decoder): T = decoder.decodeString() as T

	override fun serialize(encoder: Encoder, value: T) {
		encoder.encodeString(value.toString())
	}
}
