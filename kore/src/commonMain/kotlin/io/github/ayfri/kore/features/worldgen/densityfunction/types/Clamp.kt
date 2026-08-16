package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Restricts [input] between [min] and [max].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Clamp(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var min: Double = 0.0,
	var max: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `clamp` density function to the data pack, restricting [constant] between [min] and [max].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.clamp(fileName: String, constant: Double, min: Double = 0.0, max: Double = 0.0): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Clamp(densityFunctionOrDouble(constant), min, max))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `clamp` density function to the data pack, restricting [reference] between [min] and [max].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.clamp(
	fileName: String,
	reference: DensityFunctionArgument,
	min: Double = 0.0,
	max: Double = 0.0,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Clamp(densityFunctionOrDouble(reference), min, max))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
