package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Squeeze(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun squeeze(constant: Double) = Squeeze(densityFunctionOrDouble(constant))
fun squeeze(reference: DensityFunctionArgument) = Squeeze(densityFunctionOrDouble(reference))
