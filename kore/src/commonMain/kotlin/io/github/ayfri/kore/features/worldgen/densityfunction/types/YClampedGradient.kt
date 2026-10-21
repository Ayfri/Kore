package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import kotlinx.serialization.Serializable

/**
 * Linearly interpolates between [fromValue] and [toValue] as the input Y coordinate goes from [fromY] to
 * [toY].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class YClampedGradient(
	var fromY: Int = 0,
	var toY: Int = 0,
	var fromValue: Double = 0.0,
	var toValue: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `y_clamped_gradient` density function to the data pack, linearly interpolating between
 * [fromValue] and [toValue] as Y goes from [fromY] to [toY].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.yClampedGradient(
	fileName: String,
	fromY: Int = 0,
	toY: Int = 0,
	fromValue: Double = 0.0,
	toValue: Double = 0.0,
) = register(fileName, YClampedGradient(fromY, toY, fromValue, toValue))

/**
 * Adds a `y_clamped_gradient` density function to the data pack, linearly interpolating between
 * [YClampedGradient.fromValue] and [YClampedGradient.toValue] as Y goes from [YClampedGradient.fromY] to
 * [YClampedGradient.toY]. Configure it in [block].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.yClampedGradient(fileName: String, block: YClampedGradient.() -> Unit) =
	register(fileName, YClampedGradient().apply(block))
