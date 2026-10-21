package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Linearly interpolates between [first] and [second] by [alpha]: [first] when [alpha] is 0, [second] when it is 1.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Lerp(
	var alpha: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var first: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var second: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `lerp` density function to the data pack, interpolating from the constant [first] to the constant [second] by [alpha].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.lerp(fileName: String, alpha: DensityFunctionArgument, first: Double, second: Double) =
	register(fileName, Lerp(densityFunctionOrDouble(alpha), densityFunctionOrDouble(first), densityFunctionOrDouble(second)))

/**
 * Adds a `lerp` density function to the data pack, interpolating from the constant [first] to [second] by [alpha].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.lerp(fileName: String, alpha: DensityFunctionArgument, first: Double, second: DensityFunctionArgument) =
	register(fileName, Lerp(densityFunctionOrDouble(alpha), densityFunctionOrDouble(first), densityFunctionOrDouble(second)))

/**
 * Adds a `lerp` density function to the data pack, interpolating from [first] to the constant [second] by [alpha].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.lerp(fileName: String, alpha: DensityFunctionArgument, first: DensityFunctionArgument, second: Double) =
	register(fileName, Lerp(densityFunctionOrDouble(alpha), densityFunctionOrDouble(first), densityFunctionOrDouble(second)))

/**
 * Adds a `lerp` density function to the data pack, interpolating from [first] to [second] by [alpha].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.lerp(
	fileName: String,
	alpha: DensityFunctionArgument,
	first: DensityFunctionArgument,
	second: DensityFunctionArgument,
) = register(fileName, Lerp(densityFunctionOrDouble(alpha), densityFunctionOrDouble(first), densityFunctionOrDouble(second)))
