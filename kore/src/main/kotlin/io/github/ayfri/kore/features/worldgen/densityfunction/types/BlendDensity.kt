package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.DensityFunctionArgument
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class BlendDensity(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun blendDensity(constant: Double) = BlendDensity(densityFunctionOrDouble(constant))
fun blendDensity(reference: DensityFunctionArgument) = BlendDensity(densityFunctionOrDouble(reference))
