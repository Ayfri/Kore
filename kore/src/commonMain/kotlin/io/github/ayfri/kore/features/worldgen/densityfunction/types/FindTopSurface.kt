package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class FindTopSurface(
	var density: DensityFunctionArgument,
	var upperBound: DensityFunctionArgument,
	var lowerBound: Int,
	var cellHeight: Int,
) : DensityFunctionType()

fun findTopSurface(
	density: DensityFunctionArgument,
	upperBound: DensityFunctionArgument,
	lowerBound: Int,
	cellHeight: Int,
) = FindTopSurface(density, upperBound, lowerBound, cellHeight)
