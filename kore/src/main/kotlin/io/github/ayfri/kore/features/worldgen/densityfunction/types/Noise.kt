package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.types.resources.worldgen.DensityFunctionArgument
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import kotlinx.serialization.Serializable

@Serializable
data class Noise(
	var noise: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var xzScale: Double = 0.0,
	var yScale: Double = 0.0,
) : DensityFunctionType()

fun noise(constant: Double, xzScale: Double = 0.0, yScale: Double = 0.0) = Noise(densityFunctionOrDouble(constant), xzScale, yScale)

fun noise(
	reference: DensityFunctionArgument,
	xzScale: Double = 0.0,
	yScale: Double = 0.0,
) = Noise(densityFunctionOrDouble(reference), xzScale, yScale)
