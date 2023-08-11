package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Cache2D(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun cache2D(constant: Double) = Cache2D(densityFunctionOrDouble(constant))
fun cache2D(reference: DensityFunctionArgument) = Cache2D(densityFunctionOrDouble(reference))
