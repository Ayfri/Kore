package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Computes the reciprocal (1 / x) of [input].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Reciprocal(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `reciprocal` density function to the data pack, computing the reciprocal (1 / x) of [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.reciprocal(fileName: String, constant: Double) = register(fileName, Reciprocal(densityFunctionOrDouble(constant)))

/**
 * Adds a `reciprocal` density function to the data pack, computing the reciprocal (1 / x) of [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.reciprocal(fileName: String, reference: DensityFunctionArgument) =
	register(fileName, Reciprocal(densityFunctionOrDouble(reference)))
