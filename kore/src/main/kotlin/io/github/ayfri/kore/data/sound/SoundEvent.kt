package io.github.ayfri.kore.data.sound

import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = SoundEvent.Companion.SoundEventSerializer::class)
data class SoundEvent(
	var soundId: SoundEventArgument = SoundEventArgument.invoke("", ""),
	var range: Float? = null,
) {
	companion object {
		data object SoundEventSerializer : KSerializer<SoundEvent> {
			override val descriptor = buildClassSerialDescriptor("SoundEvent") {
				element<String>("soundId")
				element<Float>("range", isOptional = true)
			}

			override fun serialize(encoder: Encoder, value: SoundEvent) = when (value.range) {
				null -> encoder.encodeSerializableValue(SoundEventArgument.serializer(), value.soundId)
				else -> encoder.encodeStructure(descriptor) {
					encodeSerializableElement(descriptor, 0, SoundEventArgument.serializer(), value.soundId)
					encodeFloatElement(descriptor, 1, value.range!!)
				}
			}

			override fun deserialize(decoder: Decoder) = error("SoundEvent is not deserializable.")
		}
	}
}
