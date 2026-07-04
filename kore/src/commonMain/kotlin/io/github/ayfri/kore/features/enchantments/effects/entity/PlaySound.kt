package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constantFloatProvider
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class PlaySound(
	var sound: InlinableList<SoundEvent>,
	var volume: FloatProvider = constantFloatProvider(0.000_01f),
	var pitch: FloatProvider = constantFloatProvider(0.000_01f),
) : EntityEffect()

fun PlaySound.sound(vararg sounds: SoundEvent) {
	sound = sounds.toList()
}

fun PlaySound.pitch(value: Float) {
	pitch = constantFloatProvider(value)
}

fun PlaySound.volume(value: Float) {
	volume = constantFloatProvider(value)
}
