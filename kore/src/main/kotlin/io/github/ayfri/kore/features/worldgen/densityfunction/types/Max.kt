package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.DensityFunctionArgument
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Max(
	var argument1: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var argument2: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun max(constant: Double, constant2: Double) = Max(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2))
fun max(constant: Double, reference: DensityFunctionArgument) = Max(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference))
fun max(reference: DensityFunctionArgument, constant: Double) = Max(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant))
fun max(reference: DensityFunctionArgument, reference2: DensityFunctionArgument) =
	Max(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2))
