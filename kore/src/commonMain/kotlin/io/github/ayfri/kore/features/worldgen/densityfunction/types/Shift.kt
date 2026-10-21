package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise] at the input position scaled down by 4.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Shift(
	var noise: NoiseArgument,
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
fun DensityFunctionsScope.shift(fileName: String, noise: NoiseArgument) = register(fileName, Shift(noise))
