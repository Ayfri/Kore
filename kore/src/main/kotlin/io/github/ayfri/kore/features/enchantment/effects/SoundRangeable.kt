package io.github.ayfri.kore.features.enchantment.effects

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = SoundRangeable.Companion.SoundRangeableSerializer::class)
data class SoundRangeable(
	var soundId: SoundArgument = SoundArgument.invoke("", ""),
	var range: Float? = null,
) {
	companion object {
		data object SoundRangeableSerializer : KSerializer<SoundRangeable> {
			override val descriptor = buildClassSerialDescriptor("SoundRangeable") {
				element<String>("soundId")
				element<Float>("range", isOptional = true)
			}

			override fun serialize(encoder: Encoder, value: SoundRangeable) = when (value.range) {
				null -> encoder.encodeSerializableValue(SoundArgument.serializer(), value.soundId)
				else -> encoder.encodeStructure(descriptor) {
					encodeSerializableElement(descriptor, 0, SoundArgument.serializer(), value.soundId)
					encodeFloatElement(descriptor, 1, value.range!!)
				}
			}

			override fun deserialize(decoder: Decoder) = error("SoundRangeable is not deserializable.")
		}
	}
}
