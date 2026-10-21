package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import kotlinx.serialization.Serializable

/**
 * Samples the End's outer islands, without the central island. Takes no parameters.
 *
 * Vanilla builds the main island disc in `minecraft:end/islands` from `slice`, `distance_to_point` and this function.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data object EndOuterIslands : DensityFunctionType()

/**
 * Adds an `end_outer_islands` density function to the data pack, sampling the End's outer islands without the central
 * island. Takes no parameters.
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.endOuterIslands(fileName: String) = register(fileName, EndOuterIslands)
