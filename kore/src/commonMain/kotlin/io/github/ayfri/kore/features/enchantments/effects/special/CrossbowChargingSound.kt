package io.github.ayfri.kore.features.enchantments.effects.special

import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable

/**
 * The sounds a crossbow plays while being charged, one entry per enchantment level.
 *
 * Each stage is optional, so an entry can replace only the sound it cares about.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#crossbow_charging_sounds
 *
 * @property start The sound played when the charging starts, silent when `null`.
 * @property mid The sound played halfway through the charging, silent when `null`.
 * @property end The sound played when the crossbow is fully charged, silent when `null`.
 */
@Serializable
data class CrossbowChargingSound(
	var start: SoundEvent? = null,
	var mid: SoundEvent? = null,
	var end: SoundEvent? = null,
) : SpecialEnchantmentEffect()

/** Sets [CrossbowChargingSound.start], the sound played when the charging starts. */
fun CrossbowChargingSound.start(sound: SoundEventArgument, range: Float? = null) {
	start = SoundEvent(sound, range)
}

/** Sets [CrossbowChargingSound.mid], the sound played halfway through the charging. */
fun CrossbowChargingSound.mid(sound: SoundEventArgument, range: Float? = null) {
	mid = SoundEvent(sound, range)
}

/** Sets [CrossbowChargingSound.end], the sound played when the crossbow is fully charged. */
fun CrossbowChargingSound.end(sound: SoundEventArgument, range: Float? = null) {
	end = SoundEvent(sound, range)
}
