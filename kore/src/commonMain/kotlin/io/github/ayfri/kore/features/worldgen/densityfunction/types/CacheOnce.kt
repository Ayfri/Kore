package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Caches [argument], computing it once per block position even if referenced multiple times.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class CacheOnce(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `cache_once` density function to the data pack, caching [constant] so it's only computed once per
 * block position, even if referenced multiple times.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.cacheOnce(fileName: String, constant: Double): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, CacheOnce(densityFunctionOrDouble(constant)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `cache_once` density function to the data pack, caching [reference] so it's only computed once per
 * block position, even if referenced multiple times.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.cacheOnce(fileName: String, reference: DensityFunctionArgument): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, CacheOnce(densityFunctionOrDouble(reference)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}
