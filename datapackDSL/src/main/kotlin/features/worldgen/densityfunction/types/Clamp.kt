package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Clamp(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var min: Double = 0.0,
	var max: Double = 0.0,
) : DensityFunctionType()

fun clamp(constant: Double, min: Double = 0.0, max: Double = 0.0) = Clamp(densityFunctionOrDouble(constant), min, max)
fun clamp(reference: DensityFunctionArgument, min: Double = 0.0, max: Double = 0.0) = Clamp(densityFunctionOrDouble(reference), min, max)
