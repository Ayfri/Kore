package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.DensityFunctionArgument
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Spline(
	var spline: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun spline(constant: Double) = Spline(densityFunctionOrDouble(constant))
fun spline(reference: DensityFunctionArgument) = Spline(densityFunctionOrDouble(reference))
