package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class ShiftB(
	var argument: NoiseArgument,
) : DensityFunctionType()

fun shiftB(argument: NoiseArgument) = ShiftB(argument)
