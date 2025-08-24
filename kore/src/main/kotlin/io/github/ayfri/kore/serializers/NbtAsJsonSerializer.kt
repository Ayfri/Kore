package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.knbt.*

data object NbtAsJsonSerializer : KSerializer<NbtTag> {
	override val descriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: NbtTag) {
		when (encoder) {
			is NbtEncoder -> encoder.encodeNbtTag(value)
			else -> encoder.encodeSerializableValue(JsonElement.serializer(), value.toJsonElement())
		}
	}

	override fun deserialize(decoder: Decoder) = throw UnsupportedOperationException("Nbt Json deserialization is not supported")

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
