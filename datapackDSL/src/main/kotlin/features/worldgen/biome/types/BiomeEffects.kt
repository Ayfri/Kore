package features.worldgen.biome.types

import arguments.Argument
import arguments.Color
import arguments.ColorAsDecimalSerializer
import arguments.color
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
	var ambientSound: Argument.Sound? = null,
	var moodSound: MoodSound? = null,
	var additionsSound: AdditionalSound? = null,
	var music: Music? = null,
	var particle: Particle? = null,
)

fun BiomeEffects.moodSound(sound: Argument.Sound, init: MoodSound.() -> Unit = {}) {
	moodSound = MoodSound(sound).apply(init)
}

fun BiomeEffects.moodSound(sound: Argument.Sound, tickDelay: Int, blockSearchExtent: Int, offset: Float) {
	moodSound = MoodSound(sound, tickDelay, blockSearchExtent, offset)
}

fun BiomeEffects.additionsSound(sound: Argument.Sound, tickChance: Float) {
	additionsSound = AdditionalSound(sound, tickChance)
}

fun BiomeEffects.music(sound: Argument.Sound, init: Music.() -> Unit = {}) {
	music = Music(sound).apply(init)
}

fun BiomeEffects.music(sound: Argument.Sound, minDelay: Int, maxDelay: Int, replaceCurrentMusic: Boolean) {
	music = Music(sound, minDelay, maxDelay, replaceCurrentMusic)
}

fun BiomeEffects.particle(type: Argument.Particle, probability: Float) {
	particle = Particle(ParticleOptions(type), probability)
}
