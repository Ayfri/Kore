package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class FlatCache(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun flatCache(constant: Double) = FlatCache(densityFunctionOrDouble(constant))
fun flatCache(reference: DensityFunctionArgument) = FlatCache(densityFunctionOrDouble(reference))
