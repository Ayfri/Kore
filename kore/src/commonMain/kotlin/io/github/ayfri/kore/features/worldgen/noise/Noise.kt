package io.github.ayfri.kore.features.worldgen.noise

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition of the Perlin noise parameters a noise router samples, an octave being added per amplitude
 * starting at [firstOctave].
 *
 * Produces `data/<namespace>/worldgen/noise/<fileName>.json`. A [fileName] holding slashes lands in subfolders, the
 * way vanilla groups its cave and ore noises.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Noise
 *
 * @property fileName The name of the generated file, slashes creating subfolders.
 * @property firstOctave The octave the first amplitude applies to, serialized as `firstOctave` and not snake_cased.
 * @property amplitudes The amplitude of each successive octave, an amplitude of `0.0` skipping its octave.
 */
@Serializable
data class Noise(
	@Transient
	override var fileName: String = "noise",
	@JsonSerialName("firstOctave")
	var firstOctave: Int = 0,
	var amplitudes: List<Double> = emptyList(),
) : Generator("worldgen/noise") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Registers a noise named [fileName], slashes creating subfolders.
 *
 * ```kotlin
 * noise("cave/entrance") {
 *     firstOctave = -7
 *     amplitudes(0.4, 0.5, 1.0)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Noise
 */
fun DataPack.noise(fileName: String = "noise", init: Noise.() -> Unit = {}): NoiseArgument {
	val noise = Noise(fileName).apply(init)
	noises += noise
	return NoiseArgument(fileName, noise.namespace ?: name)
}

/** Sets the amplitudes to [amplitudes], one per successive octave starting at [Noise.firstOctave]. */
fun Noise.amplitudes(vararg amplitudes: Double) {
	this.amplitudes = amplitudes.toList()
}
