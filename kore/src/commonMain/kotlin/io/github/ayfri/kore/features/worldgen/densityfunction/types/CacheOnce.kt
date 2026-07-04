package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

@Serializable
data class CacheOnce(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

fun cacheOnce(constant: Double) = CacheOnce(densityFunctionOrDouble(constant))
fun cacheOnce(reference: DensityFunctionArgument) = CacheOnce(densityFunctionOrDouble(reference))
