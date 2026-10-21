package io.github.ayfri.kore.features.worldgen.noise

import kotlinx.serialization.KSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The octave layout shared by [Noise] files and inline [NoiseParameters].
 *
 * @property amplitudeModifiers The amplitude multiplier of each octave, between `0.0` and `1000000.0` and at most 32 of them. Absent or empty
 * means `1.0` for every octave, otherwise its size has to equal [octaveCount].
 * @property octaveCount The number of octaves, between `1` and `32`. Left unset, it's written as the size of [amplitudeModifiers], or
 * omitted (meaning `1`) without modifiers.
 */
sealed interface AmplitudeModifiersHolder {
	var amplitudeModifiers: List<Double>?
	var octaveCount: Int?
}

/**
 * Writes `octave_count` as the size of [AmplitudeModifiersHolder.amplitudeModifiers] when it's unset, since the game rejects a noise whose
 * modifier count differs from its octave count. An explicit [AmplitudeModifiersHolder.octaveCount] is written as is.
 */
open class AmplitudeModifiersHolderSerializer<T : AmplitudeModifiersHolder>(
	private val delegate: KSerializer<T>,
	private val withOctaveCount: T.(Int) -> T,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	override fun serialize(encoder: Encoder, value: T) {
		val count = value.amplitudeModifiers?.size?.takeIf { it > 0 && value.octaveCount == null }
		encoder.encodeSerializableValue(delegate, count?.let { value.withOctaveCount(it) } ?: value)
	}

	override fun deserialize(decoder: Decoder) = decoder.decodeSerializableValue(delegate)
}

/** Sets [AmplitudeModifiersHolder.amplitudeModifiers], one modifier per octave: `amplitudeModifiers(1.0, 0.5)` writes 2 octaves. */
fun AmplitudeModifiersHolder.amplitudeModifiers(vararg modifiers: Double) {
	amplitudeModifiers = modifiers.toList()
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
 * @property octaveCount The number of octaves, between `1` and `32`. Left unset, it's written as the size of [amplitudeModifiers], or
 * omitted (meaning `1`) without modifiers.
 */
@KeepGeneratedSerializer
@Serializable(with = NoiseParameters.Companion.NoiseParametersSerializer::class)
data class NoiseParameters(
	override var amplitudeModifiers: List<Double>? = null,
	var baseAmplitude: Double? = null,
	var baseOctave: Int = 0,
	var normalize: Boolean? = null,
	override var octaveCount: Int? = null,
) : AmplitudeModifiersHolder {
	companion object {
		data object NoiseParametersSerializer :
			AmplitudeModifiersHolderSerializer<NoiseParameters>(generatedSerializer(), { copy(octaveCount = it) })
	}
}

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
