package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/**
 * What a [Gradient] outputs past its coordinate range.
 *
 * - [CLAMP_TO_EDGE]: holds the value of the nearest edge.
 * - [MIRRORED_REPEAT]: goes back and forth between the two values.
 * - [REPEAT]: restarts the gradient.
 */
@Serializable(with = TilingMode.Companion.TilingModeSerializer::class)
enum class TilingMode {
	CLAMP_TO_EDGE,
	MIRRORED_REPEAT,
	REPEAT;

	companion object {
		data object TilingModeSerializer : LowercaseSerializer<TilingMode>(entries)
	}
}

/**
 * Linearly interpolates from [fromValue] to [toValue] as the [axis] coordinate goes from [fromCoordinate] to
 * [toCoordinate]. Both values are between `-1000000.0` and `1000000.0` and the two coordinates have to differ.
 *
 * [tiling] sets the output past that range, [TilingMode.CLAMP_TO_EDGE] when null.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Gradient(
	var axis: Axis = Axis.Y,
	var fromCoordinate: Int = 0,
	var fromValue: Double = 0.0,
	var tiling: TilingMode? = null,
	var toCoordinate: Int = 0,
	var toValue: Double = 0.0,
) : DensityFunctionType()

/**
 * Adds a `gradient` density function to the data pack, linearly interpolating from [fromValue] to [toValue] as the
 * [axis] coordinate goes from [fromCoordinate] to [toCoordinate], with [tiling] setting the output past that range.
 *
 * ```kotlin
 * gradient("height", Axis.Y, fromCoordinate = 0, toCoordinate = 128, fromValue = 1.0, toValue = -1.0)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.gradient(
	fileName: String,
	axis: Axis,
	fromCoordinate: Int,
	toCoordinate: Int,
	fromValue: Double,
	toValue: Double,
	tiling: TilingMode? = null,
) = register(fileName, Gradient(axis, fromCoordinate, fromValue, tiling, toCoordinate, toValue))

/**
 * Adds a `gradient` density function to the data pack, linearly interpolating from [Gradient.fromValue] to
 * [Gradient.toValue] along an axis. Configure it in [block].
 *
 * ```kotlin
 * gradient("x_stripes") {
 *     axis = Axis.X
 *     fromCoordinate = 0
 *     toCoordinate = 16
 *     fromValue = -1.0
 *     toValue = 1.0
 *     tiling = TilingMode.MIRRORED_REPEAT
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.gradient(fileName: String, block: Gradient.() -> Unit) = register(fileName, Gradient().apply(block))
