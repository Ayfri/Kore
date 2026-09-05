package io.github.ayfri.kore.features.enchantments.effects.entity

import io.github.ayfri.kore.data.sound.SoundEvent
import io.github.ayfri.kore.features.worldgen.floatproviders.ConstantFloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProviderScope
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Plays a sound at the position of the affected entity.
 *
 * [sound] holds one entry per enchantment level, the last one being reused for levels beyond the list.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#play_sound
 *
 * @property sound The sound played at each enchantment level.
 * @property volume How loud the sound is, from `0.00001` to `10`.
 * @property pitch How high the sound is, from `0.00001` to `2`, values below `0.5` being treated as `0.5`.
 */
@Serializable
data class PlaySound(
	var sound: InlinableList<SoundEvent>,
	var volume: FloatProvider = ConstantFloatProvider(1f),
	var pitch: FloatProvider = ConstantFloatProvider(1f),
) : EntityEffect(), FloatProviderScope

/** Sets [PlaySound.sound], one entry per enchantment level. */
fun PlaySound.sound(vararg sounds: SoundEvent) {
	sound = sounds.toList()
}

/** Sets [PlaySound.sound] to a single [sound] played at every enchantment level, audible up to [range] blocks away. */
fun PlaySound.sound(sound: SoundEventArgument, range: Float? = null) {
	this.sound = listOf(SoundEvent(sound, range))
}

/** Sets [PlaySound.pitch] to a constant [value], from `0.00001` to `2`. */
fun PlaySound.pitch(value: Float) {
	pitch = ConstantFloatProvider(value)
}

/** Sets [PlaySound.volume] to a constant [value], from `0.00001` to `10`. */
fun PlaySound.volume(value: Float) {
	volume = ConstantFloatProvider(value)
}
