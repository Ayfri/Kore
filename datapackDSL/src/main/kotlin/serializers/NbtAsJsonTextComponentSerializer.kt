package serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.knbt.*

object NbtAsJsonTextComponentSerializer : KSerializer<NbtTag> {
	override val descriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: NbtTag) =
		encoder.encodeSerializableValue(JsonElement.serializer(), value.toJsonElement())

	override fun deserialize(decoder: Decoder): NbtTag = throw UnsupportedOperationException("Deserializing not supported")

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
