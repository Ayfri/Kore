package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Adds [left] and [right] together.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Add(
	var left: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var right: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds an `add` density function to the data pack, summing [constant] and [constant2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.add(fileName: String, constant: Double, constant2: Double) =
	register(fileName, Add(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2)))

/**
 * Adds an `add` density function to the data pack, summing [constant] and [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.add(fileName: String, constant: Double, reference: DensityFunctionArgument) =
	register(fileName, Add(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference)))

/**
 * Adds an `add` density function to the data pack, summing [reference] and [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.add(fileName: String, reference: DensityFunctionArgument, constant: Double) =
	register(fileName, Add(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant)))

/**
 * Adds an `add` density function to the data pack, summing [reference] and [reference2].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.add(
	fileName: String,
	reference: DensityFunctionArgument,
	reference2: DensityFunctionArgument,
) = register(fileName, Add(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2)))
