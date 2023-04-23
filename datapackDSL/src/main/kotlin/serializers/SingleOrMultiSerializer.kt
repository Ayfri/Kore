package serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class SingleOrMultiSerializer<T>(private val kSerializer: KSerializer<T>) : KSerializer<List<T>> {
	override val descriptor = kSerializer.descriptor

	override fun deserialize(decoder: Decoder) = error("Not implemented")

	override fun serialize(encoder: Encoder, value: List<T>) = when (value.size) {
		1 -> encoder.encodeSerializableValue(kSerializer, value[0])
		else -> encoder.encodeSerializableValue(ListSerializer(kSerializer), value)
	}
}
