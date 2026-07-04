package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class ShiftA(
	var argument: NoiseArgument,
) : DensityFunctionType()

fun shiftA(argument: NoiseArgument) = ShiftA(argument)
