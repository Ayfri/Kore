package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import kotlinx.serialization.Serializable

/**
 * Supports legacy chunk compatibility blending. Takes no parameters.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data object BlendOffset : DensityFunctionType()

/**
 * Adds a `blend_offset` density function to the data pack, supporting legacy chunk compatibility
 * blending. Takes no parameters.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.blendOffset(fileName: String) = register(fileName, BlendOffset)
