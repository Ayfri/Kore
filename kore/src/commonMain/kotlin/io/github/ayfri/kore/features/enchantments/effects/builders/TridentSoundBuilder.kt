package io.github.ayfri.kore.features.enchantments.effects.builders

import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The sounds of the `trident_sound` component, played when a Riptide trident launches its holder, one entry per
 * enchantment level.
 *
 * Serialized as the bare list, the last entry being reused for levels beyond it.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#trident_sound
 */
@Serializable(with = TridentSoundBuilder.Companion.TridentSoundBuilderSerializer::class)
data class TridentSoundBuilder(var sounds: List<SoundEvent> = emptyList()) : EffectBuilder() {
	companion object {
		data object TridentSoundBuilderSerializer : InlineAutoSerializer<TridentSoundBuilder, List<SoundEvent>>(
			serializer<List<SoundEvent>>(),
			TridentSoundBuilder::sounds,
			::TridentSoundBuilder,
			serialName = "TridentSoundBuilder",
		)
	}
}

/** Appends the sound used at the next enchantment level, built in [block]. */
fun TridentSoundBuilder.sound(block: SoundEvent.() -> Unit = {}) = apply { sounds += SoundEvent().apply(block) }

/** Appends [sound] as the sound used at the next enchantment level, audible up to [range] blocks away. */
fun TridentSoundBuilder.sound(sound: SoundEventArgument, range: Float? = null) =
	apply { sounds += SoundEvent(sound, range) }
