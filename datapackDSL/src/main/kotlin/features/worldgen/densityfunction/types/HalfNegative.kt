package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class HalfNegative(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun halfNegative(constant: Double) = HalfNegative(densityFunctionOrDouble(constant))
fun halfNegative(reference: DensityFunctionArgument) = HalfNegative(densityFunctionOrDouble(reference))
