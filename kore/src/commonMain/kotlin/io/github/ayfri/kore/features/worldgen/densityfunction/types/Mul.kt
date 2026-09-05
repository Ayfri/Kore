package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Multiplies [argument1] and [argument2] together.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Mul(
	var argument1: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var argument2: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `mul` density function to the data pack, multiplying [constant] and [constant2] together.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.mul(fileName: String, constant: Double, constant2: Double): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Mul(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `mul` density function to the data pack, multiplying [constant] and [reference] together.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.mul(fileName: String, constant: Double, reference: DensityFunctionArgument): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Mul(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `mul` density function to the data pack, multiplying [reference] and [constant] together.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.mul(fileName: String, reference: DensityFunctionArgument, constant: Double): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Mul(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `mul` density function to the data pack, multiplying [reference] and [reference2] together.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.mul(
	fileName: String,
	reference: DensityFunctionArgument,
	reference2: DensityFunctionArgument,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Mul(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
