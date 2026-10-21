package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise] at the current position, scaled horizontally by [xzScale] and vertically by [yScale].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Noise(
	var noise: NoiseArgument,
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `noise` density function to the data pack, sampling [noise] scaled horizontally by [xzScale]
 * and vertically by [yScale].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.noise(
	fileName: String,
	noise: NoiseArgument,
	xzScale: Double = 0.0,
	yScale: Double = 0.0,
) = register(fileName, Noise(noise, xzScale, yScale))
