package serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

typealias InlinableList<T> = @Serializable(with = InlinableListSerializer::class) List<T>

fun InlinableListSerializer(kSerializer: KSerializer<*>): KSerializer<InlinableList<*>> = InlinableListSerializer(kSerializer)

@OptIn(ExperimentalSerializationApi::class)
open class InlinableListSerializer<T : Any>(private val kSerializer: KSerializer<T>) : KSerializer<List<T>> {
	override val descriptor = ListSerializer(JsonElement.serializer()).descriptor

	override fun deserialize(decoder: Decoder) = error("List of ${kSerializer.descriptor.serialName} cannot be deserialized")

	override fun serialize(encoder: Encoder, value: List<T>) = when (value.size) {
		1 -> encoder.encodeSerializableValue(kSerializer, value[0])
		else -> encoder.encodeSerializableValue(ListSerializer(kSerializer), value)
	}
}
