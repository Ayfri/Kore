package pack

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

/**
 * Serializes as follows:
 * If `number` is not null, serializes as `number`.
 * If `list` is not empty, serializes as `list`.
 * Else serializes as `{"min_inclusive": minInclusive, "max_inclusive": maxInclusive}`.
 */
@Serializable(with = SupportedFormats.Companion.SupportedFormatsSerializer::class)
data class SupportedFormats(
	var number: Int? = null,
	var list: List<Int>? = null,
	var minInclusive: Int? = null,
	var maxInclusive: Int? = null,
) {
	companion object {
		data object SupportedFormatsSerializer : KSerializer<SupportedFormats> {
			override val descriptor = buildClassSerialDescriptor("SupportedFormats") {
				element<Int>("min_inclusive")
				element<Int>("max_inclusive")
			}

			override fun deserialize(decoder: Decoder) = error("SupportedFormats is not deserializable")

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
