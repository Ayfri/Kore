package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise] like [Noise], but offsets the sampled coordinates by [shiftX], [shiftY] and [shiftZ]
 * before scaling.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class ShiftedNoise(
	var noise: NoiseArgument,
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
	var shiftX: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var shiftY: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var shiftZ: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `shifted_noise` density function to the data pack, sampling [noise] like a regular `noise`
 * function but with its coordinates offset by [ShiftedNoise.shiftX], [ShiftedNoise.shiftY] and
 * [ShiftedNoise.shiftZ]. Set [ShiftedNoise.xzScale], [ShiftedNoise.yScale] and the shift offsets in
 * [block].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.shiftedNoise(fileName: String, noise: NoiseArgument, block: ShiftedNoise.() -> Unit = {}): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, ShiftedNoise(noise).apply(block))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/** Sets [ShiftedNoise.shiftX] to [constant]. */
fun ShiftedNoise.shiftX(constant: Double) = run { shiftX = densityFunctionOrDouble(constant) }

/** Sets [ShiftedNoise.shiftX] to [reference]. */
fun ShiftedNoise.shiftX(reference: DensityFunctionArgument) = run { shiftX = densityFunctionOrDouble(reference) }

/** Sets [ShiftedNoise.shiftY] to [constant]. */
fun ShiftedNoise.shiftY(constant: Double) = run { shiftY = densityFunctionOrDouble(constant) }

/** Sets [ShiftedNoise.shiftY] to [reference]. */
fun ShiftedNoise.shiftY(reference: DensityFunctionArgument) = run { shiftY = densityFunctionOrDouble(reference) }

/** Sets [ShiftedNoise.shiftZ] to [constant]. */
fun ShiftedNoise.shiftZ(constant: Double) = run { shiftZ = densityFunctionOrDouble(constant) }

/** Sets [ShiftedNoise.shiftZ] to [reference]. */
fun ShiftedNoise.shiftZ(reference: DensityFunctionArgument) = run { shiftZ = densityFunctionOrDouble(reference) }
