package io.github.ayfri.kore.features.enchantment.effects.special

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.features.enchantment.effects.SoundRangeable
import kotlinx.serialization.Serializable

@Serializable
data class CrossbowChargingSound(
	var start: SoundRangeable = SoundRangeable(),
	var mid: SoundRangeable = SoundRangeable(),
	var end: SoundRangeable = SoundRangeable(),
) : SpecialEnchantmentEffect()

fun CrossbowChargingSound.start(block: SoundRangeable.() -> Unit) {
	start.apply(block)
}

fun CrossbowChargingSound.start(sound: SoundArgument, range: Float? = null) {
	start = SoundRangeable(sound, range)
}

fun CrossbowChargingSound.mid(block: SoundRangeable.() -> Unit) {
	mid.apply(block)
}

fun CrossbowChargingSound.mid(sound: SoundArgument, range: Float? = null) {
	mid = SoundRangeable(sound, range)
}

fun CrossbowChargingSound.end(block: SoundRangeable.() -> Unit) {
	end.apply(block)
}

fun CrossbowChargingSound.end(sound: SoundArgument, range: Float? = null) {
	end = SoundRangeable(sound, range)
}
