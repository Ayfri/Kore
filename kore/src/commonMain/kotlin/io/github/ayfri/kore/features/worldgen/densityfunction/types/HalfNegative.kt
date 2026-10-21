package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Returns half of [input] when it's negative, otherwise [input] unchanged.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class HalfNegative(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `half_negative` density function to the data pack, returning half of [constant] when it's
 * negative, otherwise [constant] unchanged.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.halfNegative(fileName: String, constant: Double) =
	register(fileName, HalfNegative(densityFunctionOrDouble(constant)))

/**
 * Adds a `half_negative` density function to the data pack, returning half of [reference] when it's
 * negative, otherwise [reference] unchanged.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.halfNegative(fileName: String, reference: DensityFunctionArgument) =
	register(fileName, HalfNegative(densityFunctionOrDouble(reference)))
