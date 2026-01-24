package io.github.ayfri.kore.data.sound

import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = SoundEvent.Companion.SoundEventSerializer::class)
data class SoundEvent(
	@SerialName("sound_id")
	var soundId: SoundEventArgument = SoundEventArgument.invoke("", ""),
	var range: Float? = null,
) {
	companion object {
		data object SoundEventSerializer : SinglePropertySimplifierSerializer<SoundEvent, SoundEventArgument>(
			SoundEvent::class,
			SoundEvent::soundId
		)
	}
}
