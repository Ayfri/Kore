package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Divides [left] by [right], returning 0 when [left] is 0.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Div(
	var left: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var right: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `div` density function to the data pack, dividing [constant] by [constant2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.div(fileName: String, constant: Double, constant2: Double) =
	register(fileName, Div(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2)))

/**
 * Adds a `div` density function to the data pack, dividing [constant] by [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.div(fileName: String, constant: Double, reference: DensityFunctionArgument) =
	register(fileName, Div(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference)))

/**
 * Adds a `div` density function to the data pack, dividing [reference] by [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.div(fileName: String, reference: DensityFunctionArgument, constant: Double) =
	register(fileName, Div(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant)))

/**
 * Adds a `div` density function to the data pack, dividing [reference] by [reference2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.div(
	fileName: String,
	reference: DensityFunctionArgument,
	reference2: DensityFunctionArgument,
) = register(fileName, Div(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2)))
