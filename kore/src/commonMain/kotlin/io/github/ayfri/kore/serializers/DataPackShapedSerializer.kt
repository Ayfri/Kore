package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.DataPack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder

/**
 * Writes a data pack type nested in a component the way it is written in a data pack file, in SNBT and in JSON: snake_case
 * keys (`ifTrue` of a block state provider rule is `if_true`) and the keys of [JsonSerialName] (`Name` of a block state).
 *
 * ```kotlin
 * data object BlockStateProviderComponentSerializer : DataPackShapedSerializer<BlockStateProvider>(BlockStateProvider.serializer())
 * ```
 */
open class DataPackShapedSerializer<T>(private val serializer: KSerializer<T>) : KSerializer<T> {
	override val descriptor = serializer.descriptor

	override fun serialize(encoder: Encoder, value: T) {
		val element = DataPack.jsonFormat.encodeToJsonElement(serializer, value)
		when (encoder) {
			is JsonEncoder -> encoder.encodeJsonElement(element)
			else -> encoder.nbtEncoder?.encodeNbtTag(NbtAsJsonSerializer.fromJson(element, valueBasedNumbers = true))
				?: error("${serializer.descriptor.serialName} can only be serialized to JSON or NBT.")
		}
	}

	override fun deserialize(decoder: Decoder): T = with(DataPack.jsonFormat) {
		when (decoder) {
			is JsonDecoder -> decodeFromJsonElement(serializer, decoder.decodeJsonElement())
			else -> decoder.nbtDecoder?.let { decodeFromJsonElement(serializer, encodeToJsonElement(NbtAsJsonSerializer, it.decodeNbtTag())) }
				?: error("${serializer.descriptor.serialName} can only be deserialized from JSON or NBT.")
		}
	}
}
