package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Scans a column for the topmost position where [density] is above zero, searching between [lowerBound]
 * and [upperBound] in steps of [cellHeight].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class FindTopSurface(
	var density: DensityFunctionArgument,
	var upperBound: DensityFunctionArgument,
	var lowerBound: Int,
	var cellHeight: Int,
) : DensityFunctionType()

/**
 * Adds a `find_top_surface` density function to the data pack, scanning a column for the topmost position
 * where [density] is above zero, between [lowerBound] and [upperBound] in steps of [cellHeight].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.findTopSurface(
	fileName: String,
	density: DensityFunctionArgument,
	upperBound: DensityFunctionArgument,
	lowerBound: Int,
	cellHeight: Int,
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, FindTopSurface(density, upperBound, lowerBound, cellHeight))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
