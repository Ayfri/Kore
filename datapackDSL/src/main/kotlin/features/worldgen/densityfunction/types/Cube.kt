package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Cube(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun cube(constant: Double) = Cube(densityFunctionOrDouble(constant))
fun cube(reference: DensityFunctionArgument) = Cube(densityFunctionOrDouble(reference))
