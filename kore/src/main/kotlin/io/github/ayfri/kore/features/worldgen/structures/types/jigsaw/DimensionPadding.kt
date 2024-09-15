package io.github.ayfri.kore.features.worldgen.structures.types.jigsaw

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable
data class DimensionPadding(
	var value: Int? = null,
	var top: Int? = null,
	var bottom: Int? = null,
) {
	companion object {
		data object DimensionPaddingSerializer : KSerializer<DimensionPadding> {
			override val descriptor = buildClassSerialDescriptor("DimensionPadding") {
				element<Int>("top")
				element<Int>("bottom")
			}

			override fun serialize(encoder: Encoder, value: DimensionPadding) = when {
				value.value != null -> encoder.encodeInt(value.value!!)
				else -> encoder.encodeStructure(descriptor) {
					encodeIntElement(descriptor, 0, value.top!!)
					encodeIntElement(descriptor, 1, value.bottom!!)
				}
			}

			override fun deserialize(decoder: Decoder) = error("DimensionPadding is not deserializable.")
		}
	}
}
