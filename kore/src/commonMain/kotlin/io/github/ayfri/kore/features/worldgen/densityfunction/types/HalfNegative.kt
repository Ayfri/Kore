package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Returns half of [argument] when it's negative, otherwise [argument] unchanged.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class HalfNegative(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `half_negative` density function to the data pack, returning half of [constant] when it's
 * negative, otherwise [constant] unchanged.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.halfNegative(fileName: String, constant: Double): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, HalfNegative(densityFunctionOrDouble(constant)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `half_negative` density function to the data pack, returning half of [reference] when it's
 * negative, otherwise [reference] unchanged.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.halfNegative(fileName: String, reference: DensityFunctionArgument): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, HalfNegative(densityFunctionOrDouble(reference)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
