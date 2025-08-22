package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = BreakSound.Companion.BreakSoundSerializer::class)
data class BreakSound(var sound: SoundEventArgument) : Component() {
	companion object {
		data object BreakSoundSerializer : InlineAutoSerializer<BreakSound>(BreakSound::class)
	}
}

fun ComponentsScope.breakSound(sound: SoundEventArgument) = apply { this[ItemComponentTypes.BREAK_SOUND] = BreakSound(sound) }
