package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Rounds [input] up to a multiple of [multiple], as `ceil(input / multiple) * multiple`.
 *
 * [multiple] defaults to 1 and isn't written when `null`. [input] is returned unchanged when [multiple] is 0.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Ceil(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
	var multiple: DensityFunctionOrDouble? = null,
) : DensityFunctionType()

/**
 * Adds a `ceil` density function to the data pack, rounding [constant] up to a multiple of the constant [multiple]
 * (1 when `null`).
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.ceil(fileName: String, constant: Double, multiple: Double? = null) =
	register(fileName, Ceil(densityFunctionOrDouble(constant), multiple?.let(::densityFunctionOrDouble)))

/**
 * Adds a `ceil` density function to the data pack, rounding [constant] up to a multiple of [multiple].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.ceil(fileName: String, constant: Double, multiple: DensityFunctionArgument) =
	register(fileName, Ceil(densityFunctionOrDouble(constant), densityFunctionOrDouble(multiple)))

/**
 * Adds a `ceil` density function to the data pack, rounding [reference] up to a multiple of the constant [multiple]
 * (1 when `null`).
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.ceil(fileName: String, reference: DensityFunctionArgument, multiple: Double? = null) =
	register(fileName, Ceil(densityFunctionOrDouble(reference), multiple?.let(::densityFunctionOrDouble)))

/**
 * Adds a `ceil` density function to the data pack, rounding [reference] up to a multiple of [multiple].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.ceil(fileName: String, reference: DensityFunctionArgument, multiple: DensityFunctionArgument) =
	register(fileName, Ceil(densityFunctionOrDouble(reference), densityFunctionOrDouble(multiple)))
