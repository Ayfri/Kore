package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import kotlinx.serialization.Serializable

@Serializable
data class AdditionalSound(var sound: SoundArgument, var tickChance: Float)

@Serializable
data class MoodSound(
	var sound: SoundArgument? = null,
	var tickDelay: Int = 6000,
	var blockSearchExtent: Int = 8,
	var offset: Float = 2f,
)

@Serializable
data class AmbientSounds(
	var additions: List<AdditionalSound>? = null,
	var loop: SoundArgument? = null,
	var mood: MoodSound? = null,
) : EnvironmentAttributesType()

fun EnvironmentAttributesScope.ambientSounds(
	additions: List<AdditionalSound>? = null,
	loop: SoundArgument? = null,
	mood: MoodSound? = null,
	mod: EnvironmentAttributeModifier? = null,
	block: AmbientSounds.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Audio.AMBIENT_SOUNDS] =
		environmentAttributeValue(AmbientSounds(additions, loop, mood).apply(block), mod)
}

fun AmbientSounds.additions(vararg list: AdditionalSound) = apply {
	additions = (additions ?: listOf()) + list
}

fun AmbientSounds.addition(sound: SoundArgument, tickChance: Float) = additions(AdditionalSound(sound, tickChance))

fun AmbientSounds.mood(
	sound: SoundArgument? = null,
	tickDelay: Int = 6000,
	blockSearchExtent: Int = 8,
	offset: Float = 2f,
	block: MoodSound.() -> Unit = {},
) = apply {
	mood = MoodSound(sound, tickDelay, blockSearchExtent, offset).apply(block)
}
