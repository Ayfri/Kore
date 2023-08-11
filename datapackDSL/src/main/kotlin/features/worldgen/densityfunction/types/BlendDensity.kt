package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.DensityFunctionArgument
import features.worldgen.densityfunction.DensityFunctionOrDouble
import features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class BlendDensity(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun blendDensity(constant: Double) = BlendDensity(densityFunctionOrDouble(constant))
fun blendDensity(reference: DensityFunctionArgument) = BlendDensity(densityFunctionOrDouble(reference))
