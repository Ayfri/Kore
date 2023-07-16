package features.worldgen.biome.types

import arguments.colors.Color
import arguments.colors.ColorAsDecimalSerializer
import arguments.colors.color
import arguments.types.resources.ParticleArgument
import arguments.types.resources.SoundArgument
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
	var music: Music? = null,
	var particle: Particle? = null,
)

fun BiomeEffects.moodSound(sound: SoundArgument, init: MoodSound.() -> Unit = {}) {
	moodSound = MoodSound(sound).apply(init)
}

fun BiomeEffects.moodSound(sound: SoundArgument, tickDelay: Int, blockSearchExtent: Int, offset: Float) {
	moodSound = MoodSound(sound, tickDelay, blockSearchExtent, offset)
}

fun BiomeEffects.additionsSound(sound: SoundArgument, tickChance: Float) {
	additionsSound = AdditionalSound(sound, tickChance)
}

fun BiomeEffects.music(sound: SoundArgument, init: Music.() -> Unit = {}) {
	music = Music(sound).apply(init)
}

fun BiomeEffects.music(sound: SoundArgument, minDelay: Int, maxDelay: Int, replaceCurrentMusic: Boolean) {
	music = Music(sound, minDelay, maxDelay, replaceCurrentMusic)
}

fun BiomeEffects.particle(type: ParticleArgument, probability: Float) {
	particle = Particle(ParticleOptions(type), probability)
}
