package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Raises [input] to the power of 2 (x^2).
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Square(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `square` density function to the data pack, outputting [constant] raised to the power of 2 (x^2).
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.square(fileName: String, constant: Double) = register(fileName, Square(densityFunctionOrDouble(constant)))

/**
 * Adds a `square` density function to the data pack, outputting [reference] raised to the power of 2 (x^2).
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.square(fileName: String, reference: DensityFunctionArgument) =
	register(fileName, Square(densityFunctionOrDouble(reference)))
