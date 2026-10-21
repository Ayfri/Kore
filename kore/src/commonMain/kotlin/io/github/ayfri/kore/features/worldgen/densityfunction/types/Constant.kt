package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import kotlinx.serialization.Serializable

/**
 * Returns [value], ignoring the input position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Constant(
	var value: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `constant` density function to the data pack, always outputting [value] regardless of the
 * input position.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.constant(fileName: String, value: Double) = register(fileName, Constant(value))
