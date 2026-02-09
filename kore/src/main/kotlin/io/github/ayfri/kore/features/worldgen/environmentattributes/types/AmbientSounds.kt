package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable

/** A sound that will be randomly played around the camera based on tick chance. */
@Serializable
data class AdditionalSound(var sound: SoundEventArgument, var tickChance: Float)

/** A mood sound that plays randomly based on surrounding darkness levels. */
@Serializable
data class MoodSound(
	var sound: SoundEventArgument? = null,
	var tickDelay: Int = 6000,
	var blockSearchExtent: Int = 8,
	var offset: Float = 2f,
)

/** Controls ambient sounds played around the camera: looping, mood-based, and additional random sounds. */
@Serializable
data class AmbientSounds(
	var additions: List<AdditionalSound>? = null,
	var loop: SoundEventArgument? = null,
	var mood: MoodSound? = null,
) : EnvironmentAttributesType()

/** Sets the ambient sounds attribute, controlling looping, mood, and additional sounds. */
fun EnvironmentAttributesScope.ambientSounds(
	additions: List<AdditionalSound>? = null,
	loop: SoundEventArgument? = null,
	mood: MoodSound? = null,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: AmbientSounds.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Audio.AMBIENT_SOUNDS] =
		environmentAttributeValue(AmbientSounds(additions, loop, mood).apply(block), mod)
}

fun AmbientSounds.additions(vararg list: AdditionalSound) = apply {
	additions = (additions ?: listOf()) + list
}

fun AmbientSounds.addition(sound: SoundEventArgument, tickChance: Float) = additions(AdditionalSound(sound, tickChance))

fun AmbientSounds.mood(
	sound: SoundEventArgument? = null,
	tickDelay: Int = 6000,
	blockSearchExtent: Int = 8,
	offset: Float = 2f,
	block: MoodSound.() -> Unit = {},
) = apply {
	mood = MoodSound(sound, tickDelay, blockSearchExtent, offset).apply(block)
}
