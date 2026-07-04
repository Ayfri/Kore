package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlendDensity(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun blendDensity(constant: Double) = BlendDensity(densityFunctionOrDouble(constant))
fun blendDensity(reference: DensityFunctionArgument) = BlendDensity(densityFunctionOrDouble(reference))
