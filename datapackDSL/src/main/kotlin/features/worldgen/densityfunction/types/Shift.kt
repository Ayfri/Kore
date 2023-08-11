package features.worldgen.densityfunction.types

import arguments.types.resources.worldgen.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class Shift(
	var argument: NoiseArgument,
) : DensityFunctionType()

fun shift(argument: NoiseArgument) = Shift(argument)
