package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class ShiftB(
	var argument: NoiseArgument,
) : DensityFunctionType()

fun shiftB(argument: NoiseArgument) = ShiftB(argument)
