package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.NoiseArgument
import kotlinx.serialization.Serializable

/**
 * Samples [argument] at the input position scaled down by 4.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Shift(
	var argument: NoiseArgument,
) : DensityFunctionType()

/**
 * Adds a `shift` density function to the data pack, sampling [argument] at the input position scaled down
 * by 4.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.shift(fileName: String, argument: NoiseArgument): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Shift(argument))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
