package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*
import kotlin.math.abs
import kotlin.math.floor

/** [NbtAsJsonSerializer] for a field typed [NbtCompound]: a JSON object in JSON, a compound in NBT. */
data object NbtCompoundAsJsonSerializer : KSerializer<NbtCompound> {
	override val descriptor = NbtAsJsonSerializer.descriptor

	override fun serialize(encoder: Encoder, value: NbtCompound) = NbtAsJsonSerializer.serialize(encoder, value)

	override fun deserialize(decoder: Decoder) = NbtAsJsonSerializer.deserialize(decoder) as NbtCompound
}

data object NbtAsJsonSerializer : KSerializer<NbtTag> {
	override val descriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: NbtTag) {
		encoder.nbtEncoder?.encodeNbtTag(value) ?: encoder.encodeSerializableValue(JsonElement.serializer(), value.toJsonElement())
	}

	override fun deserialize(decoder: Decoder) = when (decoder) {
		is JsonDecoder -> decoder.decodeJsonElement().toNbtTag()
		else -> decoder.nbtDecoder?.decodeNbtTag() ?: throw UnsupportedOperationException("NbtAsJsonSerializer can only be deserialized from Json or Nbt.")
	}

	/**
	 * Converts [element] to NBT, deciding each number tag from its value when [valueBasedNumbers] is set: a whole number is an int tag
	 * (long outside the Int range), any other a double tag, and a list mixing numeric tags is promoted to doubles (longs without doubles).
	 * `1.0` is written `1` on JS, so the default decides from the JSON text and differs between platforms.
	 */
	fun fromJson(element: JsonElement, valueBasedNumbers: Boolean) = element.toNbtTag(valueBasedNumbers)

	/** NBT lists hold a single tag type, so a mixed JSON array falls back to a list of strings. */
	private fun JsonElement.toNbtTag(valueBasedNumbers: Boolean = false): NbtTag = when (this) {
		is JsonObject -> NbtCompound(mapValues { it.value.toNbtTag(valueBasedNumbers) })
		is JsonArray -> map { it.toNbtTag(valueBasedNumbers) }.let { elements ->
			when {
				elements.distinctBy { it::class }.size <= 1 -> NbtList.of(*elements.toTypedArray())
				valueBasedNumbers && elements.all { it is NbtInt || it is NbtLong || it is NbtDouble } ->
					if (elements.any { it is NbtDouble }) NbtList(elements.map { NbtDouble(it.numberValue().toDouble()) })
					else NbtList(elements.map { NbtLong(it.numberValue().toLong()) })

				else -> NbtList(elements.map { it as? NbtString ?: NbtString(it.toString()) })
			}
		}

		is JsonPrimitive -> when {
			isString -> NbtString(content)
			else -> booleanOrNull?.let(::NbtByte)
				?: longOrNull?.toIntOrLongTag()
				?: doubleOrNull?.let { if (valueBasedNumbers && abs(it) < 9.0E18 && it == floor(it)) it.toLong().toIntOrLongTag() else NbtDouble(it) }
				?: NbtString(content)
		}
	}

	private fun Long.toIntOrLongTag() = if (this in Int.MIN_VALUE..Int.MAX_VALUE) NbtInt(toInt()) else NbtLong(this)

	private fun NbtTag.numberValue(): Number = when (this) {
		is NbtInt -> value
		is NbtLong -> value
		else -> (this as NbtDouble).value
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
