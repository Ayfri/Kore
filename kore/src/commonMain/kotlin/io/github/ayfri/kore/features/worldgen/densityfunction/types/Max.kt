package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Returns the larger of [argument1] and [argument2].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Max(
	var argument1: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var argument2: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `max` density function to the data pack, returning the larger of [constant] and [constant2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.max(fileName: String, constant: Double, constant2: Double) =
	register(fileName, Max(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2)))

/**
 * Adds a `max` density function to the data pack, returning the larger of [constant] and [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.max(fileName: String, constant: Double, reference: DensityFunctionArgument) =
	register(fileName, Max(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference)))

/**
 * Adds a `max` density function to the data pack, returning the larger of [reference] and [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.max(fileName: String, reference: DensityFunctionArgument, constant: Double) =
	register(fileName, Max(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant)))

/**
 * Adds a `max` density function to the data pack, returning the larger of [reference] and [reference2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.max(
	fileName: String,
	reference: DensityFunctionArgument,
	reference2: DensityFunctionArgument,
) = register(fileName, Max(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2)))
