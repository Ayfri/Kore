package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Returns the smaller of [left] and [right].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Min(
	var left: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var right: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `min` density function to the data pack, returning the smaller of [constant] and [constant2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.min(fileName: String, constant: Double, constant2: Double) =
	register(fileName, Min(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2)))

/**
 * Adds a `min` density function to the data pack, returning the smaller of [constant] and [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.min(fileName: String, constant: Double, reference: DensityFunctionArgument) =
	register(fileName, Min(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference)))

/**
 * Adds a `min` density function to the data pack, returning the smaller of [reference] and [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.min(fileName: String, reference: DensityFunctionArgument, constant: Double) =
	register(fileName, Min(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant)))

/**
 * Adds a `min` density function to the data pack, returning the smaller of [reference] and [reference2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.min(
	fileName: String,
	reference: DensityFunctionArgument,
	reference2: DensityFunctionArgument,
) = register(fileName, Min(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2)))
