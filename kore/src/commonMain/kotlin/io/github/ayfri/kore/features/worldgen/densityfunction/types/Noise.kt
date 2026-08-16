package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Samples [noise], scaled horizontally by [xzScale] and vertically by [yScale].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Noise(
	var noise: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `noise` density function to the data pack, sampling [constant] scaled horizontally by [xzScale]
 * and vertically by [yScale].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.noise(fileName: String, constant: Double, xzScale: Double = 0.0, yScale: Double = 0.0): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Noise(densityFunctionOrDouble(constant), xzScale, yScale))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `noise` density function to the data pack, sampling [reference] scaled horizontally by [xzScale]
 * and vertically by [yScale].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.noise(
	fileName: String,
	reference: DensityFunctionArgument,
	xzScale: Double = 0.0,
	yScale: Double = 0.0,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Noise(densityFunctionOrDouble(reference), xzScale, yScale))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
