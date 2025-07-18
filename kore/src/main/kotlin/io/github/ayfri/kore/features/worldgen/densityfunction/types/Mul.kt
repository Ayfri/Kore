package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class Mul(
	var argument1: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var argument2: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun mul(constant: Double, constant2: Double) = Mul(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2))
fun mul(constant: Double, reference: DensityFunctionArgument) = Mul(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference))
fun mul(reference: DensityFunctionArgument, constant: Double) = Mul(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant))
