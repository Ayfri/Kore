package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class CacheAllInCell(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun cacheAllInCell(constant: Double) = CacheAllInCell(densityFunctionOrDouble(constant))
fun cacheAllInCell(reference: DensityFunctionArgument) = CacheAllInCell(densityFunctionOrDouble(reference))
