package io.github.ayfri.kore.features.worldgen.densityfunction

import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * This class represents a noise file ID or noise parameters written inline.
 * Needed because the noise-sampling density functions accept both.
 *
 * @property noise The noise file ID. It can be null if the instance holds inline parameters.
 * @property parameters The inline noise parameters. It can be null if the instance holds a noise file ID.
 */
@KeepGeneratedSerializer
@Serializable(with = NoiseOrParameters.Companion.NoiseOrParametersSerializer::class)
data class NoiseOrParameters(
	var noise: NoiseArgument? = null,
	var parameters: NoiseParameters? = null,
) {
	companion object {
		data object NoiseOrParametersSerializer :
			EitherInlineSerializer<NoiseOrParameters>(generatedSerializer(), "noise", "parameters")
	}
}

/**
 * References the noise file [noise].
 *
 * ```
 * noiseOrParameters(Noises.CALCITE)  // "minecraft:calcite"
 * ```
 */
fun noiseOrParameters(noise: NoiseArgument) = NoiseOrParameters(noise = noise)

/**
 * Writes the noise [parameters] inline.
 *
 * ```
 * noiseOrParameters(noiseParameters(-3, 1.0, 1.0))  // {"amplitude_modifiers": [1.0, 1.0], "base_octave": -3, "octave_count": 2}
 * ```
 */
fun noiseOrParameters(parameters: NoiseParameters) = NoiseOrParameters(parameters = parameters)
