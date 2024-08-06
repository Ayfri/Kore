package io.github.ayfri.kore.features.enchantment.effects.entity.spawnparticles

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = ParticlePosition.Companion.ParticlePositionSerializer::class)
data class ParticlePosition(
	var type: ParticlePositionType,
	var offset: Float? = null,
	var scale: Float? = null,
) {
	companion object {
		data object ParticlePositionSerializer : KSerializer<ParticlePosition> {
			override val descriptor = buildClassSerialDescriptor("ParticlePosition") {
				element<String>("type")
				element<Float?>("offset")
				element<Float?>("scale")
			}

			@OptIn(ExperimentalSerializationApi::class)
			override fun serialize(encoder: Encoder, value: ParticlePosition) =
				encoder.encodeStructure(descriptor) {
					encodeSerializableElement(descriptor, 0, ParticlePositionType.serializer(), value.type)
					encodeNullableSerializableElement(descriptor, 1, Float.serializer(), value.offset)
					if (value.type == ParticlePositionType.IN_BOUNDING_BOX) {
						// Only encode `scale` if the `type` is `IN_BOUNDING_BOX`.
						encodeNullableSerializableElement(descriptor, 2, Float.serializer(), value.scale)
					}
				}

			override fun deserialize(decoder: Decoder) = error("ParticlePosition is not meant to be deserialized")
		}
	}
}
