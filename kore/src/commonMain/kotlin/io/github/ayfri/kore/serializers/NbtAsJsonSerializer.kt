package io.github.ayfri.kore.serializers

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

	/** NBT lists hold a single tag type, so a mixed JSON array falls back to a list of strings. */
	private fun JsonElement.toNbtTag(): NbtTag = when (this) {
		is JsonObject -> NbtCompound(mapValues { it.value.toNbtTag() })
		is JsonArray -> map { it.toNbtTag() }.let { elements ->
			if (elements.distinctBy { it::class }.size <= 1) NbtList.of(*elements.toTypedArray())
			else NbtList(elements.map { it as? NbtString ?: NbtString(it.toString()) })
		}

		is JsonPrimitive -> when {
			isString -> NbtString(content)
			else -> booleanOrNull?.let(::NbtByte)
				?: longOrNull?.let { if (it in Int.MIN_VALUE..Int.MAX_VALUE) NbtInt(it.toInt()) else NbtLong(it) }
				?: doubleOrNull?.let(::NbtDouble)
				?: NbtString(content)
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
