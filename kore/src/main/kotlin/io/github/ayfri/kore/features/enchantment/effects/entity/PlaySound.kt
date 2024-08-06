package io.github.ayfri.kore.features.enchantment.effects.entity

import io.github.ayfri.kore.features.enchantment.effects.SoundRangeable
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.constantFloatProvider
import kotlinx.serialization.Serializable

@Serializable
data class PlaySound(
	var sound: SoundRangeable,
	var volume: FloatProvider = constantFloatProvider(0.000_01f),
	var pitch: FloatProvider = constantFloatProvider(0.000_01f),
) : EntityEffect()

fun PlaySound.volume(value: Float) {
	volume = constantFloatProvider(value)
}

fun PlaySound.pitch(value: Float) {
	pitch = constantFloatProvider(value)
}
