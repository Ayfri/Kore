package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class Invert(
	var argument: DensityFunctionArgument,
) : DensityFunctionType()

fun invert(argument: DensityFunctionArgument) = Invert(argument)
