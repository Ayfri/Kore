package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.decodeFromNbtTag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import net.benwoodworth.knbt.NbtList

typealias InlinableList<T> = @Serializable(with = InlinableListSerializer::class) List<T>

open class InlinableListSerializer<T>(private val kSerializer: KSerializer<T>) : KSerializer<List<T>> {
	private val listSerializer = ListSerializer(kSerializer)
	override val descriptor = listSerializer.descriptor

	override fun deserialize(decoder: Decoder) = when (decoder) {
		is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
			is JsonArray -> element.map { decoder.json.decodeFromJsonElement(kSerializer, it) }
			else -> listOf(decoder.json.decodeFromJsonElement(kSerializer, element))
		}

		else -> decoder.nbtDecoder?.let { nbt ->
			when (val tag = nbt.decodeNbtTag()) {
				is NbtList<*> -> tag.map { decodeFromNbtTag(kSerializer, it) }
				else -> listOf(decodeFromNbtTag(kSerializer, tag))
			}
		} ?: decoder.decodeSerializableValue(listSerializer)
	}

	override fun serialize(encoder: Encoder, value: List<T>) = when (value.size) {
		1 -> encoder.encodeSerializableValue(kSerializer, value[0])
		else -> encoder.encodeSerializableValue(listSerializer, value)
	}
}
