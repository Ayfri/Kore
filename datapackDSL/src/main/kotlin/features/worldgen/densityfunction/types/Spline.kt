package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Spline(
	var spline: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun spline(constant: Double) = Spline(densityFunctionOrDouble(constant))
fun spline(reference: DensityFunctionArgument) = Spline(densityFunctionOrDouble(reference))
