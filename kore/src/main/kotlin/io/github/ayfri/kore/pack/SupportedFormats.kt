package io.github.ayfri.kore.pack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*

/**
 * Represents the supported pack formats for a Minecraft data pack or resource pack.
 *
 * Serialization rules:
 * - If `number` is not null, serializes as a single integer (the pack format).
 * - If `list` is not empty, serializes as a list of integers (multiple supported formats).
 * - Otherwise, serializes as an object with `"min_inclusive"` and `"max_inclusive"` properties, representing a supported range.
 *
 * JSON format reference: https://minecraft.wiki/w/Pack_format
 */
@Serializable(with = SupportedFormats.Companion.SupportedFormatsSerializer::class)
data class SupportedFormats(
	var number: Int? = null,
	var list: List<Int>? = null,
	var minInclusive: Int? = null,
	var maxInclusive: Int? = null,
) {
	/** Checks if the given value is in the supported formats. */
	fun isInRange(value: Int) = when {
		number != null -> value == number
		list != null -> value in list!!
		minInclusive != null && maxInclusive != null -> value in minInclusive!!..maxInclusive!!
		else -> false
	}

	/** Checks if the given range is in the supported formats. */
	fun isInRange(value: IntRange) = when {
		number != null -> value.first == number && value.last == number
		list != null -> value.all { it in list!! }
		minInclusive != null && maxInclusive != null -> value.first in minInclusive!!..maxInclusive!! && value.last in minInclusive!!..maxInclusive!!
		else -> false
	}

	/** Checks if the given supported formats are compatible. */
	fun isCompatibleWith(other: SupportedFormats) = when {
		number != null && other.number != null -> number == other.number
		list != null && other.list != null -> list!!.intersect(other.list!!.toSet()).isNotEmpty()
		minInclusive != null && maxInclusive != null && other.minInclusive != null && other.maxInclusive != null -> minInclusive!! in other.minInclusive!!..other.maxInclusive!! || maxInclusive!! in other.minInclusive!!..other.maxInclusive!!
		else -> false
	}

	override fun toString() = when {
		number != null -> number.toString()
		list != null -> list.toString()
		minInclusive != null && maxInclusive != null -> "$minInclusive..$maxInclusive"
		else -> "Unsupported format"
	}

	companion object {
		data object SupportedFormatsSerializer : KSerializer<SupportedFormats> {
			override val descriptor = buildClassSerialDescriptor("SupportedFormats") {
				element<Int>("min_inclusive")
				element<Int>("max_inclusive")
			}

			override fun deserialize(decoder: Decoder): SupportedFormats {
				require(decoder is JsonDecoder) { "SupportedFormats can only be deserialized by JSON" }
				return when (val element = decoder.decodeJsonElement()) {
					is JsonPrimitive -> when {
						element.intOrNull != null -> SupportedFormats(number = element.int)
						else -> throw SerializationException("Expected integer for SupportedFormats")
					}

					is JsonArray -> {
						SupportedFormats(list = element.jsonArray.map {
							it.jsonPrimitive.int
						})
					}

					is JsonObject -> {
						val minInclusive = element["min_inclusive"]?.jsonPrimitive?.int
						val maxInclusive = element["max_inclusive"]?.jsonPrimitive?.int
						SupportedFormats(minInclusive = minInclusive, maxInclusive = maxInclusive)
					}

					else -> throw SerializationException("Unknown SupportedFormats type")
				}
			}

			override fun serialize(encoder: Encoder, value: SupportedFormats) = when {
				value.number != null -> encoder.encodeInt(value.number!!)
				value.list != null -> encoder.encodeSerializableValue(ListSerializer(Int.serializer()), value.list!!)
				else -> encoder.encodeStructure(descriptor) {
					if (value.minInclusive != null) encodeIntElement(descriptor, 0, value.minInclusive!!)
					if (value.maxInclusive != null) encodeIntElement(descriptor, 1, value.maxInclusive!!)
				}
			}
		}
	}
}
