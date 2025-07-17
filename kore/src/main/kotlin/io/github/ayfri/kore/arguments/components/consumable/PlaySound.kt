package io.github.ayfri.kore.arguments.components.consumable

import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable

@Serializable
data class PlaySound(
	var sound: SoundEventArgument,
) : ConsumeEffect()

fun ConsumeEffects.playSound(sound: SoundEventArgument) = apply { effects += PlaySound(sound) }
