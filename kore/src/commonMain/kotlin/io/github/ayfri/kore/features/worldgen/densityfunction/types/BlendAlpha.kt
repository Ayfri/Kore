package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import kotlinx.serialization.Serializable

/**
 * Facilitates smooth transitions between chunk generation versions. Takes no parameters.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data object BlendAlpha : DensityFunctionType()

/**
 * Adds a `blend_alpha` density function to the data pack, facilitating smooth transitions between chunk
 * generation versions. Takes no parameters.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.blendAlpha(fileName: String) = register(fileName, BlendAlpha)
