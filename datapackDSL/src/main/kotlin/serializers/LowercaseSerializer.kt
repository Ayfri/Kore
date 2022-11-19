package serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class LowercaseSerializer<T>(private val values: Array<T>) : KSerializer<T> where T : Enum<T> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LowercaseSerializer", PrimitiveKind.STRING)
	
	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeString()
		return values.first { it.name.lowercase() == value }
	}
	
	override fun serialize(encoder: Encoder, value: T) {
		encoder.encodeString(value.name.lowercase())
	}
}
