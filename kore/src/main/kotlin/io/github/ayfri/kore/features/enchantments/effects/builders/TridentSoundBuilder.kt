package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = TridentSoundBuilder.Companion.TridentSoundBuilderSerializer::class)
data class TridentSoundBuilder(
	var sounds: List<SoundEvent> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object TridentSoundBuilderSerializer : InlineSerializer<TridentSoundBuilder, List<SoundEvent>>(
			kSerializer = ListSerializer(SoundEvent.serializer()),
			property = TridentSoundBuilder::sounds
		)
	}
}

fun TridentSoundBuilder.sound(block: SoundEvent.() -> Unit) {
	sounds += SoundEvent().apply(block)
}

fun TridentSoundBuilder.sound(sound: SoundEventArgument, range: Float? = null) {
	sounds += SoundEvent(sound, range)
}
