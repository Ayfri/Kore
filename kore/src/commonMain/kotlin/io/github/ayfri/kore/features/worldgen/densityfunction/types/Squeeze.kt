package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Clamps [argument] to [-1, 1], then applies `x/2 - x^3/24`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Squeeze(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `squeeze` density function to the data pack, clamping [constant] to [-1, 1] then applying
 * `x/2 - x^3/24`.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.squeeze(fileName: String, constant: Double) = register(fileName, Squeeze(densityFunctionOrDouble(constant)))

/**
 * Adds a `squeeze` density function to the data pack, clamping [reference] to [-1, 1] then applying
 * `x/2 - x^3/24`.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.squeeze(fileName: String, reference: DensityFunctionArgument) =
	register(fileName, Squeeze(densityFunctionOrDouble(reference)))
