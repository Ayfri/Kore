package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.DensityFunctionArgument
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Min(
	var argument1: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var argument2: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun min(constant: Double, constant2: Double) = Min(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2))
fun min(constant: Double, reference: DensityFunctionArgument) = Min(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference))
fun min(reference: DensityFunctionArgument, constant: Double) = Min(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant))
fun min(reference: DensityFunctionArgument, reference2: DensityFunctionArgument) =
	Min(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2))
