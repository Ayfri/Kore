package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class QuarterNegative(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun quarterNegative(constant: Double) = QuarterNegative(densityFunctionOrDouble(constant))
fun quarterNegative(reference: DensityFunctionArgument) = QuarterNegative(densityFunctionOrDouble(reference))
