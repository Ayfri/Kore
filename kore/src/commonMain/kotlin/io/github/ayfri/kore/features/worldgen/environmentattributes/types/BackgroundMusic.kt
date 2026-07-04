package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.generated.arguments.types.SoundEventArgument
import kotlinx.serialization.Serializable

/** A single music track definition with sound, delay range, and optional replacement behavior. */
@Serializable
data class MusicTrack(
	var sound: SoundEventArgument,
	var minDelay: Int,
	var maxDelay: Int,
	var replaceCurrentMusic: Boolean? = null,
)

/** Controls how and which background music is played, with optional creative and underwater overrides. */
@Serializable
data class BackgroundMusic(
	var default: MusicTrack? = null,
	var creative: MusicTrack? = null,
	var underwater: MusicTrack? = null,
) : EnvironmentAttributesType()

/** Sets the background music attribute, controlling which music tracks are played. */
fun EnvironmentAttributesScope.backgroundMusic(
	default: MusicTrack? = null,
	creative: MusicTrack? = null,
	underwater: MusicTrack? = null,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: BackgroundMusic.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Audio.BACKGROUND_MUSIC] =
		environmentAttributeValue(BackgroundMusic(default, creative, underwater).apply(block), mod)
}

fun BackgroundMusic.default(
	sound: SoundEventArgument,
	minDelay: Int = 0,
	maxDelay: Int = 0,
	replaceCurrentMusic: Boolean? = null,
) = apply {
	default = MusicTrack(sound, minDelay, maxDelay, replaceCurrentMusic)
}

fun BackgroundMusic.creative(
	sound: SoundEventArgument,
	minDelay: Int = 0,
	maxDelay: Int = 0,
	replaceCurrentMusic: Boolean? = null,
) = apply {
	creative = MusicTrack(sound, minDelay, maxDelay, replaceCurrentMusic)
}

fun BackgroundMusic.underwater(
	sound: SoundEventArgument,
	minDelay: Int = 0,
	maxDelay: Int = 0,
	replaceCurrentMusic: Boolean? = null,
) = apply {
	underwater = MusicTrack(sound, minDelay, maxDelay, replaceCurrentMusic)
}
