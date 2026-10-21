package io.github.ayfri.kore.features.worldgen.noise

import kotlinx.serialization.Serializable

/**
 * The octave layout shared by [Noise] files and inline [NoiseParameters].
 *
 * @property amplitudeModifiers The amplitude multiplier of each octave, between `0.0` and `1000000.0` and at most 32 of them. Absent or empty
 * means `1.0` for every octave, otherwise its size has to equal [octaveCount].
 * @property octaveCount The number of octaves, between `1` and `32` (default `1`).
 */
sealed interface AmplitudeModifiersHolder {
	var amplitudeModifiers: List<Double>?
	var octaveCount: Int?
}

/**
 * Sets [AmplitudeModifiersHolder.amplitudeModifiers] and [AmplitudeModifiersHolder.octaveCount] together, one modifier per octave:
 * `amplitudeModifiers(1.0, 0.5)` gives 2 octaves.
 */
fun AmplitudeModifiersHolder.amplitudeModifiers(vararg modifiers: Double) {
	amplitudeModifiers = modifiers.toList()
	octaveCount = modifiers.size
}

/**
 * Perlin noise parameters written inline, as in the noise-based block state providers.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Noise
 *
 * @property amplitudeModifiers The amplitude multiplier of each octave, between `0.0` and `1000000.0` and at most 32 of them, a modifier of
 * `0.0` skipping its octave. Absent or empty means `1.0` for every octave, otherwise its size has to equal [octaveCount].
 * @property baseAmplitude The amplitude every octave is scaled by, between `0.0` and `1000000.0` (default `1.0`).
 * @property baseOctave The octave the first modifier applies to, between `-32` and `32`, negative values being larger scales.
 * @property normalize Whether the sampled value is normalized to the `-1.0` to `1.0` range (default `true`). The deprecated `"legacy"`
 * normalization isn't expressible.
 * @property octaveCount The number of octaves, between `1` and `32` (default `1`).
 */
@Serializable
data class NoiseParameters(
	override var amplitudeModifiers: List<Double>? = null,
	var baseAmplitude: Double? = null,
	var baseOctave: Int = 0,
	var normalize: Boolean? = null,
	override var octaveCount: Int? = null,
) : AmplitudeModifiersHolder

/**
 * Creates noise parameters starting at [baseOctave], one octave per modifier of [amplitudeModifiers] (a single octave when none is given).
 *
 * ```kotlin
 * noiseParameters(-3, 1.0, 1.0)  // base_octave -3, octave_count 2, amplitude_modifiers [1.0, 1.0]
 * noiseParameters(-3)            // base_octave -3
 * ```
 */
fun noiseParameters(baseOctave: Int, vararg amplitudeModifiers: Double) =
	NoiseParameters(baseOctave = baseOctave).apply { if (amplitudeModifiers.isNotEmpty()) amplitudeModifiers(*amplitudeModifiers) }

/**
 * Creates noise parameters starting at [baseOctave], configured in [block].
 *
 * ```kotlin
 * noiseParameters(-3) { amplitudeModifiers(1.0, 0.5) }
 * noiseParameters { normalize = false }
 * ```
 */
fun noiseParameters(baseOctave: Int = 0, block: NoiseParameters.() -> Unit) = NoiseParameters(baseOctave = baseOctave).apply(block)
