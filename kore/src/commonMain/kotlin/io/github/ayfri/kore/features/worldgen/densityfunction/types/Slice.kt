package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionOrDouble
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.features.worldgen.densityfunction.densityFunctionOrDouble
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import kotlinx.serialization.Serializable

/**
 * Evaluates [input] with the [axis] coordinate fixed at [coordinate], flattening a 3D function to 2D.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Slice(
	var axis: Axis = Axis.Y,
	var coordinate: Int = 0,
	var input: DensityFunctionOrDouble = densityFunctionOrDouble(0.0),
) : DensityFunctionType()

/**
 * Adds a `slice` density function to the data pack, evaluating [reference] with the [axis] coordinate fixed at
 * [coordinate], flattening a 3D function to 2D.
 *
 * ```kotlin
 * slice("end/disc", Axis.Y, 0, distance)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.slice(fileName: String, axis: Axis, coordinate: Int, reference: DensityFunctionArgument) =
	register(fileName, Slice(axis, coordinate, densityFunctionOrDouble(reference)))
