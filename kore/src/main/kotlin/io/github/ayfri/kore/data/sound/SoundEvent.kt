package io.github.ayfri.kore.data.sound

import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = SoundEvent.Companion.SoundEventSerializer::class)
data class SoundEvent(
	@SerialName("sound_id")
	var soundId: SoundEventArgument = SoundEventArgument("", ""),
	var range: Float? = null,
) {
	companion object {
		data object SoundEventSerializer :
			SinglePropertySimplifierSerializer<SoundEvent>(generatedSerializer(), "sound_id")
	}
}
