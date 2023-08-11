package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class CacheOnce(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun cacheOnce(constant: Double) = CacheOnce(densityFunctionOrDouble(constant))
fun cacheOnce(reference: DensityFunctionArgument) = CacheOnce(densityFunctionOrDouble(reference))
