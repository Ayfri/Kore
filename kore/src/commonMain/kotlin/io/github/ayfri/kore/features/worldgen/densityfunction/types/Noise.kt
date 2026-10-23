package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.NoiseOrParameters
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.noiseOrParameters
import io.github.ayfri.kore.features.worldgen.noise.NoiseParameters
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise], a noise file ID or inline parameters, at the current position, scaled horizontally by [xzScale] and
 * vertically by [yScale].
 *
 * [shiftX], [shiftY] and [shiftZ] offset the sampled coordinates before scaling, a missing one is a constant 0.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Noise(
	var noise: NoiseOrParameters,
	var shiftX: DensityFunctionOrDouble? = null,
	var shiftY: DensityFunctionOrDouble? = null,
	var shiftZ: DensityFunctionOrDouble? = null,
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `noise` density function to the data pack, sampling [noise] scaled horizontally by [xzScale]
 * and vertically by [yScale]. Offset the sampled coordinates with [Noise.shiftX], [Noise.shiftY] and [Noise.shiftZ] in [block].
 *
 * ```kotlin
 * noise("hills", Noises.CALCITE, xzScale = 0.25) { shiftX(DensityFunctions.SHIFT_X) }
 * ```
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
	block: Noise.() -> Unit = {},
) = register(fileName, Noise(noiseOrParameters(noise), xzScale = xzScale, yScale = yScale).apply(block))

/**
 * Adds a `noise` density function to the data pack, sampling the inline noise [parameters] scaled horizontally by
 * [xzScale] and vertically by [yScale]. Offset the sampled coordinates with [Noise.shiftX], [Noise.shiftY] and [Noise.shiftZ] in [block].
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
	block: Noise.() -> Unit = {},
) = register(fileName, Noise(noiseOrParameters(parameters), xzScale = xzScale, yScale = yScale).apply(block))

/** Sets [Noise.shiftX] to [constant]. */
fun Noise.shiftX(constant: Double) = run { shiftX = densityFunctionOrDouble(constant) }

/** Sets [Noise.shiftX] to [reference]. */
fun Noise.shiftX(reference: DensityFunctionArgument) = run { shiftX = densityFunctionOrDouble(reference) }

/** Sets [Noise.shiftY] to [constant]. */
fun Noise.shiftY(constant: Double) = run { shiftY = densityFunctionOrDouble(constant) }

/** Sets [Noise.shiftY] to [reference]. */
fun Noise.shiftY(reference: DensityFunctionArgument) = run { shiftY = densityFunctionOrDouble(reference) }

/** Sets [Noise.shiftZ] to [constant]. */
fun Noise.shiftZ(constant: Double) = run { shiftZ = densityFunctionOrDouble(constant) }

/** Sets [Noise.shiftZ] to [reference]. */
fun Noise.shiftZ(reference: DensityFunctionArgument) = run { shiftZ = densityFunctionOrDouble(reference) }
