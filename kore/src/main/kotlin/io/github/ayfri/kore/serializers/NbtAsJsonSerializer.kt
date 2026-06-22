package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.nbtListOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*

data object NbtAsJsonSerializer : KSerializer<NbtTag> {
	override val descriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: NbtTag) {
		when (encoder) {
			is NbtEncoder -> encoder.encodeNbtTag(value)
			else -> encoder.encodeSerializableValue(JsonElement.serializer(), value.toJsonElement())
		}
	}

	override fun deserialize(decoder: Decoder) = when (decoder) {
		is NbtDecoder -> decoder.decodeNbtTag()
		is JsonDecoder -> decoder.decodeJsonElement().toNbtTag()
		else -> throw UnsupportedOperationException("NbtAsJsonSerializer can only be deserialized from Json or Nbt.")
	}

	private fun JsonElement.toNbtTag(): NbtTag = when (this) {
		is JsonObject -> buildNbtCompound { this@toNbtTag.forEach { (key, value) -> put(key, value.toNbtTag()) } }
		is JsonArray -> map { it.toNbtTag() }.let { elements ->
			if (elements.all { it is NbtCompound }) nbtListOf(elements.filterIsInstance<NbtCompound>())
			else nbtListOf(elements.map { (it as? NbtString)?.value ?: it.toString() })
		}

		is JsonPrimitive -> when {
			isString -> NbtString(content)
			booleanOrNull != null -> NbtByte((if (boolean) 1 else 0).toByte())
			longOrNull != null -> long.let {
				if (it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) NbtInt(it.toInt()) else NbtLong(
					it
				)
			}

			doubleOrNull != null -> NbtDouble(double)
			else -> NbtString(content)
		}
	}

	private fun NbtTag.toJsonElement(): JsonElement = when (this) {
		is NbtCompound -> JsonObject(mapValues { it.value.toJsonElement() })
		is NbtList<*> -> JsonArray(map { it.toJsonElement() })
		is NbtByteArray -> JsonArray(map { JsonPrimitive(it) })
		is NbtIntArray -> JsonArray(map { JsonPrimitive(it) })
		is NbtLongArray -> JsonArray(map { JsonPrimitive(it) })

		is NbtString -> JsonPrimitive(value)

		is NbtByte -> when (value) {
			0.toByte() -> JsonPrimitive(false)
			1.toByte() -> JsonPrimitive(true)
			else -> JsonPrimitive(value)
		}

		is NbtShort -> JsonPrimitive(value)
		is NbtInt -> JsonPrimitive(value)
		is NbtLong -> JsonPrimitive(value)
		is NbtFloat -> JsonPrimitive(value)
		is NbtDouble -> JsonPrimitive(value)
	}
}
