package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Interpolates [input] across the surrounding grid cells, [cellSizeXz] blocks wide and [cellSizeY] blocks tall.
 *
 * Both cell sizes are at least 1. Vanilla uses `4` and `8` in the Overworld, `8` and `4` in the End.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Interpolated(
	var cellSizeXz: Int,
	var cellSizeY: Int,
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds an `interpolated` density function to the data pack, interpolating [constant] across the
 * surrounding grid cells, [cellSizeXz] blocks wide and [cellSizeY] blocks tall.
 *
 * ```kotlin
 * interpolated("smooth", 0.5, cellSizeXz = 4, cellSizeY = 8)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.interpolated(fileName: String, constant: Double, cellSizeXz: Int, cellSizeY: Int) =
	register(fileName, Interpolated(cellSizeXz, cellSizeY, densityFunctionOrDouble(constant)))

/**
 * Adds an `interpolated` density function to the data pack, interpolating [reference] across the
 * surrounding grid cells, [cellSizeXz] blocks wide and [cellSizeY] blocks tall.
 *
 * ```kotlin
 * interpolated("smooth_terrain", DensityFunctions.Overworld.BASE_3D_NOISE, cellSizeXz = 4, cellSizeY = 8)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.interpolated(fileName: String, reference: DensityFunctionArgument, cellSizeXz: Int, cellSizeY: Int) =
	register(fileName, Interpolated(cellSizeXz, cellSizeY, densityFunctionOrDouble(reference)))
