package io.github.ayfri.kore.features.enchantment.effects.builders

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.features.enchantment.effects.SoundRangeable
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = TridentSoundBuilder.Companion.TridentSoundBuilderSerializer::class)
data class TridentSoundBuilder(
	var sounds: List<SoundRangeable> = emptyList(),
) : EffectBuilder() {
	companion object {
		data object TridentSoundBuilderSerializer : InlineSerializer<TridentSoundBuilder, List<SoundRangeable>>(
			kSerializer = ListSerializer(SoundRangeable.serializer()),
			property = TridentSoundBuilder::sounds
		)
	}
}

fun TridentSoundBuilder.sound(block: SoundRangeable.() -> Unit) {
	sounds += SoundRangeable().apply(block)
}

fun TridentSoundBuilder.sound(sound: SoundArgument, range: Float? = null) {
	sounds += SoundRangeable(sound, range)
}
