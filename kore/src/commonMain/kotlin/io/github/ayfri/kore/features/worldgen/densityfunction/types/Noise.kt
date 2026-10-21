package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.NoiseOrParameters
import io.github.ayfri.kore.features.worldgen.densityfunction.noiseOrParameters
import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise], a noise file ID or inline parameters, at the current position, scaled horizontally by [xzScale] and
 * vertically by [yScale].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Noise(
	var noise: NoiseOrParameters,
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
) = register(fileName, Noise(noiseOrParameters(noise), xzScale, yScale))

/**
 * Adds a `noise` density function to the data pack, sampling the inline noise [parameters] scaled horizontally by
 * [xzScale] and vertically by [yScale].
 *
 * ```kotlin
 * noise("hills", noiseParameters(-7, 1.0, 0.5), xzScale = 0.25, yScale = 0.0)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.noise(
	fileName: String,
	parameters: NoiseParameters,
	xzScale: Double = 0.0,
	yScale: Double = 0.0,
) = register(fileName, Noise(noiseOrParameters(parameters), xzScale, yScale))
