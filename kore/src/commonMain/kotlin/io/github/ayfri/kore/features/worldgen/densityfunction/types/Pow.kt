package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Raises [base] to the power of [exponent].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Pow(
	var base: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var exponent: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `pow` density function to the data pack, raising the constant [base] to the constant [exponent].
 *
 * ```kotlin
 * pow("half_power", 2.0, 0.5)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.pow(fileName: String, base: Double, exponent: Double) =
	register(fileName, Pow(densityFunctionOrDouble(base), densityFunctionOrDouble(exponent)))

/**
 * Adds a `pow` density function to the data pack, raising the constant [base] to the density function [exponent].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.pow(fileName: String, base: Double, exponent: DensityFunctionArgument) =
	register(fileName, Pow(densityFunctionOrDouble(base), densityFunctionOrDouble(exponent)))

/**
 * Adds a `pow` density function to the data pack, raising the density function [base] to the constant [exponent].
 *
 * ```kotlin
 * pow("squared_noise", DensityFunctions.Overworld.BASE_3D_NOISE, 2.0)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.pow(fileName: String, base: DensityFunctionArgument, exponent: Double) =
	register(fileName, Pow(densityFunctionOrDouble(base), densityFunctionOrDouble(exponent)))

/**
 * Adds a `pow` density function to the data pack, raising the density function [base] to the density function [exponent].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.pow(fileName: String, base: DensityFunctionArgument, exponent: DensityFunctionArgument) =
	register(fileName, Pow(densityFunctionOrDouble(base), densityFunctionOrDouble(exponent)))
