package io.github.ayfri.kore.features.enchantment.effects.special

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.data.sound.SoundEvent
import kotlinx.serialization.Serializable

@Serializable
data class CrossbowChargingSound(
	var start: SoundEvent = SoundEvent(),
	var mid: SoundEvent = SoundEvent(),
	var end: SoundEvent = SoundEvent(),
) : SpecialEnchantmentEffect()

fun CrossbowChargingSound.start(block: SoundEvent.() -> Unit) {
	start.apply(block)
}

fun CrossbowChargingSound.start(sound: SoundArgument, range: Float? = null) {
	start = SoundEvent(sound, range)
}

fun CrossbowChargingSound.mid(block: SoundEvent.() -> Unit) {
	mid.apply(block)
}

fun CrossbowChargingSound.mid(sound: SoundArgument, range: Float? = null) {
	mid = SoundEvent(sound, range)
}

fun CrossbowChargingSound.end(block: SoundEvent.() -> Unit) {
	end.apply(block)
}

fun CrossbowChargingSound.end(sound: SoundArgument, range: Float? = null) {
	end = SoundEvent(sound, range)
}
