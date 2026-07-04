package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:break_sound` item component, which sets the sound played when the item breaks from durability loss.
 *
 * Serializes as the sound event directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#break_sound
 */
@Serializable(with = BreakSound.Companion.BreakSoundSerializer::class)
data class BreakSound(var sound: SoundEventArgument) : Component() {
	companion object {
		data object BreakSoundSerializer : InlineAutoSerializer<BreakSound, SoundEventArgument>(
			serializer<SoundEventArgument>(),
			BreakSound::sound,
			::BreakSound
		)
	}
}

/** Specifies the sound played when the item breaks from durability loss. */
fun ComponentsScope.breakSound(sound: SoundEventArgument) = apply { this[ItemComponentTypes.BREAK_SOUND] = BreakSound(sound) }
