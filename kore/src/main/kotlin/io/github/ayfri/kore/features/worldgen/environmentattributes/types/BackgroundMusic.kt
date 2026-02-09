package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import kotlinx.serialization.Serializable

@Serializable
data class MusicTrack(
	var sound: SoundArgument,
	var minDelay: Int,
	var maxDelay: Int,
	var replaceCurrentMusic: Boolean? = null,
)

@Serializable
data class BackgroundMusic(
	var default: MusicTrack? = null,
	var creative: MusicTrack? = null,
	var underwater: MusicTrack? = null,
) : EnvironmentAttributesType()

fun EnvironmentAttributesScope.backgroundMusic(
	default: MusicTrack? = null,
	creative: MusicTrack? = null,
	underwater: MusicTrack? = null,
	mod: EnvironmentAttributeModifier? = null,
	block: BackgroundMusic.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Audio.BACKGROUND_MUSIC] =
		environmentAttributeValue(BackgroundMusic(default, creative, underwater).apply(block), mod)
}

fun BackgroundMusic.default(
	sound: SoundArgument,
	minDelay: Int = 0,
	maxDelay: Int = 0,
	replaceCurrentMusic: Boolean? = null,
) = apply {
	default = MusicTrack(sound, minDelay, maxDelay, replaceCurrentMusic)
}

fun BackgroundMusic.creative(
	sound: SoundArgument,
	minDelay: Int = 0,
	maxDelay: Int = 0,
	replaceCurrentMusic: Boolean? = null,
) = apply {
	creative = MusicTrack(sound, minDelay, maxDelay, replaceCurrentMusic)
}

fun BackgroundMusic.underwater(
	sound: SoundArgument,
	minDelay: Int = 0,
	maxDelay: Int = 0,
	replaceCurrentMusic: Boolean? = null,
) = apply {
	underwater = MusicTrack(sound, minDelay, maxDelay, replaceCurrentMusic)
}
