package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.NoiseOrParameters
import io.github.ayfri.kore.features.worldgen.densityfunction.noiseOrParameters
import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise], a noise file ID or inline parameters, at (z / 4, x / 4, 0), then scales the result back up by 4.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class ShiftB(
	var noise: NoiseOrParameters,
) : DensityFunctionType()

/**
 * Adds a `shift_b` density function to the data pack, sampling [noise] at (z / 4, x / 4, 0) then
 * scaling the result back up by 4.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.shiftB(fileName: String, noise: NoiseArgument) = register(fileName, ShiftB(noiseOrParameters(noise)))

/**
 * Adds a `shift_b` density function to the data pack, sampling the inline noise [parameters] at (z / 4, x / 4, 0)
 * then scaling the result back up by 4.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.shiftB(fileName: String, parameters: NoiseParameters) =
	register(fileName, ShiftB(noiseOrParameters(parameters)))
