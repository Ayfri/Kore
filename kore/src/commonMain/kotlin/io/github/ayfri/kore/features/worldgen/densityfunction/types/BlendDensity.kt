package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Enables version-to-version chunk transition blending of [argument].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class BlendDensity(
	var argument: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `blend_density` density function to the data pack, blending [constant] across chunk generation
 * version transitions (e.g. old chunks near newly generated ones).
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.blendDensity(fileName: String, constant: Double) =
	register(fileName, BlendDensity(densityFunctionOrDouble(constant)))

/**
 * Adds a `blend_density` density function to the data pack, blending [reference] across chunk generation
 * version transitions (e.g. old chunks near newly generated ones).
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.blendDensity(fileName: String, reference: DensityFunctionArgument) =
	register(fileName, BlendDensity(densityFunctionOrDouble(reference)))
