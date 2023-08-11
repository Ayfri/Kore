package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Interpolated(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun interpolated(constant: Double) = Interpolated(densityFunctionOrDouble(constant))
fun interpolated(reference: DensityFunctionArgument) = Interpolated(densityFunctionOrDouble(reference))
