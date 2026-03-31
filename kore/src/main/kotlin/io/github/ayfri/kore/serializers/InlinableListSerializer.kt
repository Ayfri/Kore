package io.github.ayfri.kore.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtList

typealias InlinableList<T> = @Serializable(with = InlinableListSerializer::class) List<T>

fun <T> inlinableListSerializer(kSerializer: KSerializer<T>) = InlinableListSerializer(kSerializer)

@OptIn(ExperimentalSerializationApi::class)
open class InlinableListSerializer<T>(private val kSerializer: KSerializer<T>) : KSerializer<List<T>> {
	override val descriptor = ListSerializer(kSerializer).descriptor

	override fun deserialize(decoder: Decoder) = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			if (element is JsonArray) {
				element.map { decoder.json.decodeFromJsonElement(kSerializer, it) }
			} else {
				listOf(decoder.json.decodeFromJsonElement(kSerializer, element))
			}
		}

		is NbtDecoder -> {
			val tag = decoder.decodeNbtTag()
			if (tag is NbtList<*>) {
				tag.map { decoder.nbt.decodeFromNbtTag(kSerializer, it) }
			} else {
				listOf(decoder.nbt.decodeFromNbtTag(kSerializer, tag))
			}
		}

		else -> decoder.decodeSerializableValue(ListSerializer(kSerializer))
	}

	override fun serialize(encoder: Encoder, value: List<T>) = when (value.size) {
		1 -> encoder.encodeSerializableValue(kSerializer, value[0])
		else -> encoder.encodeSerializableValue(ListSerializer(kSerializer), value)
	}
}
