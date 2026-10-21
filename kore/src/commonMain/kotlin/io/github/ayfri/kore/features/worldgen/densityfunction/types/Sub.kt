package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Subtracts [right] from [left].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Sub(
	var left: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var right: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `sub` density function to the data pack, subtracting [constant2] from [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.sub(fileName: String, constant: Double, constant2: Double) =
	register(fileName, Sub(densityFunctionOrDouble(constant), densityFunctionOrDouble(constant2)))

/**
 * Adds a `sub` density function to the data pack, subtracting [reference] from [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.sub(fileName: String, constant: Double, reference: DensityFunctionArgument) =
	register(fileName, Sub(densityFunctionOrDouble(constant), densityFunctionOrDouble(reference)))

/**
 * Adds a `sub` density function to the data pack, subtracting [constant] from [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.sub(fileName: String, reference: DensityFunctionArgument, constant: Double) =
	register(fileName, Sub(densityFunctionOrDouble(reference), densityFunctionOrDouble(constant)))

/**
 * Adds a `sub` density function to the data pack, subtracting [reference2] from [reference].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.sub(
	fileName: String,
	reference: DensityFunctionArgument,
	reference2: DensityFunctionArgument,
) = register(fileName, Sub(densityFunctionOrDouble(reference), densityFunctionOrDouble(reference2)))
