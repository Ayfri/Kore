package io.github.ayfri.kore.features.worldgen.biome.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.ColorAsDecimalSerializer
import io.github.ayfri.kore.arguments.colors.color
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.generated.arguments.types.ParticleTypeArgument
import kotlinx.serialization.Serializable

@Serializable
data class BiomeEffects(
	@Serializable(ColorAsDecimalSerializer::class) var skyColor: Color = color(7907327),
	@Serializable(ColorAsDecimalSerializer::class) var fogColor: Color = color(12638463),
	@Serializable(ColorAsDecimalSerializer::class) var waterColor: Color = color(4159204),
	@Serializable(ColorAsDecimalSerializer::class) var waterFogColor: Color = color(329011),
	@Serializable(ColorAsDecimalSerializer::class) var grassColor: Color? = null,
	@Serializable(ColorAsDecimalSerializer::class) var foliageColor: Color? = null,
	var grassColorModifier: GrassColorModifier? = null,
	var ambientSound: SoundArgument? = null,
	var moodSound: MoodSound? = null,
	var additionsSound: AdditionalSound? = null,
	var music: List<WeightedMusic>? = null,
	var musicVolume: Float = 1.0f,
	var particle: Particle? = null,
)

fun BiomeEffects.moodSound(sound: SoundArgument, init: MoodSound.() -> Unit = {}) {
	moodSound = MoodSound(sound).apply(init)
}

fun BiomeEffects.moodSound(sound: SoundArgument, tickDelay: Int, blockSearchExtent: Int, offset: Float) {
	moodSound = MoodSound(sound, tickDelay, blockSearchExtent, offset)
}

fun BiomeEffects.additionalSound(sound: SoundArgument, tickChance: Float) {
	additionsSound = AdditionalSound(sound, tickChance)
}

fun BiomeEffects.music(sound: SoundArgument, weight: Int = 1, init: Music.() -> Unit = {}) {
	music = listOf(WeightedMusic(Music(sound).apply(init), weight))
}

fun BiomeEffects.music(sound: SoundArgument, weight: Int = 1, minDelay: Int, maxDelay: Int, replaceCurrentMusic: Boolean) {
	music = listOf(WeightedMusic(Music(sound, minDelay, maxDelay, replaceCurrentMusic), weight))
}

fun BiomeEffects.addMusic(sound: SoundArgument, weight: Int = 1, init: Music.() -> Unit = {}) {
	music = (music ?: emptyList()) + WeightedMusic(Music(sound).apply(init), weight)
}

fun BiomeEffects.addMusic(sound: SoundArgument, weight: Int = 1, minDelay: Int, maxDelay: Int, replaceCurrentMusic: Boolean) {
	music = (music ?: emptyList()) + WeightedMusic(Music(sound, minDelay, maxDelay, replaceCurrentMusic), weight)
}

fun BiomeEffects.particle(type: ParticleTypeArgument, probability: Float) {
	particle = Particle(ParticleOptions(type), probability)
}
