package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

@Serializable
data class Shift(
	var argument: NoiseArgument,
) : DensityFunctionType()

fun shift(argument: NoiseArgument) = Shift(argument)
