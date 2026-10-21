package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.NoiseOrParameters
import io.github.ayfri.kore.features.worldgen.densityfunction.noiseOrParameters
import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise], a noise file ID or inline parameters, at the input position scaled down by 4.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Shift(
	var noise: NoiseOrParameters,
) : DensityFunctionType()

/**
 * Adds a `shift` density function to the data pack, sampling [noise] at the input position scaled down
 * by 4.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.shift(fileName: String, noise: NoiseArgument) = register(fileName, Shift(noiseOrParameters(noise)))

/**
 * Adds a `shift` density function to the data pack, sampling the inline noise [parameters] at the input position
 * scaled down by 4.
 *
 * ```kotlin
 * shift("shift_inline", noiseParameters(-3, 1.0, 1.0))
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.shift(fileName: String, parameters: NoiseParameters) =
	register(fileName, Shift(noiseOrParameters(parameters)))
