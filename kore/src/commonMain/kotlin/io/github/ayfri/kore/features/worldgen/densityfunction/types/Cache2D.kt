package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Caches [input], computing it once per horizontal (X/Z) position.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Cache2D(
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `cache_2d` density function to the data pack, caching [constant] so it's only computed once per
 * horizontal (X/Z) position.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.cache2D(fileName: String, constant: Double) = register(fileName, Cache2D(densityFunctionOrDouble(constant)))

/**
 * Adds a `cache_2d` density function to the data pack, caching [reference] so it's only computed once per
 * horizontal (X/Z) position.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.cache2D(fileName: String, reference: DensityFunctionArgument) =
	register(fileName, Cache2D(densityFunctionOrDouble(reference)))
