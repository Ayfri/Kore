package io.github.ayfri.kore.features.worldgen.noise

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition of the Perlin noise parameters a noise router samples, an octave being added per
 * [amplitudeModifiers] entry starting at [baseOctave].
 *
 * Produces `data/<namespace>/worldgen/noise/<fileName>.json`. A [fileName] holding slashes lands in subfolders, the
 * way vanilla groups its cave and ore noises.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Noise
 *
 * @property fileName The name of the generated file, slashes creating subfolders.
 * @property amplitudeModifiers The amplitude multiplier of each octave, between `0.0` and `1000000.0` and at most 32 of them, a modifier of
 * `0.0` skipping its octave. Absent or empty means `1.0` for every octave, otherwise its size has to equal [octaveCount].
 * @property baseAmplitude The amplitude every octave is scaled by, between `0.0` and `1000000.0` (default `1.0`).
 * @property baseOctave The octave the first modifier applies to, between `-32` and `32`, negative values being larger scales.
 * @property normalize Whether the sampled value is normalized to the `-1.0` to `1.0` range (default `true`). The deprecated `"legacy"`
 * normalization isn't expressible.
 * @property octaveCount The number of octaves, between `1` and `32` (default `1`).
 */
@Serializable
data class Noise(
	@Transient
	override var fileName: String = "noise",
	override var amplitudeModifiers: List<Double>? = null,
	var baseAmplitude: Double? = null,
	var baseOctave: Int = 0,
	var normalize: Boolean? = null,
	override var octaveCount: Int? = null,
) : Generator("worldgen/noise"), AmplitudeModifiersHolder {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Registers a noise named [fileName], slashes creating subfolders.
 *
 * ```kotlin
 * noise("cave/entrance") {
 *     baseOctave = -7
 *     amplitudeModifiers(0.4, 0.5, 1.0)
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
