package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunction
import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.generated.arguments.worldgen.types.DensityFunctionArgument
import io.github.ayfri.kore.serializers.EitherInlineSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * Computes a cubic spline over [spline].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class Spline(
	var spline: SplineValue,
) : DensityFunctionType()

/**
 * Either a constant value or a nested [CubicSpline], serialized inline as the one that is set.
 *
 * @property constant The constant value, `null` when this is a nested spline.
 * @property spline The nested spline, `null` when this is a constant.
 */
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = SplineValue.Companion.SplineValueSerializer::class)
data class SplineValue(
	var constant: Float? = null,
	var spline: CubicSpline? = null,
) {
	companion object {
		data object SplineValueSerializer : EitherInlineSerializer<SplineValue>(generatedSerializer(), "constant", "spline")
	}
}

/**
 * A cubic spline sampling [coordinate] and interpolating between its [points].
 *
 * @property coordinate The density function the spline is evaluated against.
 * @property points The control points, ordered by increasing [SplinePoint.location].
 */
@Serializable
data class CubicSpline(
	var coordinate: DensityFunctionArgument,
	var points: List<SplinePoint> = emptyList(),
)

/**
 * A single control point of a [CubicSpline].
 *
 * @property location The coordinate value this point sits at.
 * @property value The value at this point, either a constant or a nested spline.
 * @property derivative The slope of the curve at this point.
 */
@Serializable
data class SplinePoint(
	var location: Float,
	var value: SplineValue,
	var derivative: Float = 0.0f,
)

/**
 * Adds a `spline` density function to the data pack, interpolating [points] over [coordinate].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.spline(
	fileName: String,
	coordinate: DensityFunctionArgument,
	block: MutableList<SplinePoint>.() -> Unit = {},
): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Spline(SplineValue(spline = CubicSpline(coordinate, buildList(block)))))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/**
 * Adds a `spline` density function to the data pack, always outputting [constant].
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/noise
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.spline(fileName: String, constant: Float): DensityFunctionArgument {
	val densityFunction = DensityFunction(fileName, Spline(SplineValue(constant = constant)))
	dp.densityFunctions += densityFunction
	return DensityFunctionArgument(fileName, densityFunction.namespace ?: dp.name)
}

/** Appends a control point holding the constant [value]. */
fun MutableList<SplinePoint>.point(location: Float, value: Float, derivative: Float = 0.0f) =
	apply { add(SplinePoint(location, SplineValue(constant = value), derivative)) }

/** Appends a control point holding a nested spline over [coordinate]. */
fun MutableList<SplinePoint>.point(
	location: Float,
	coordinate: DensityFunctionArgument,
	derivative: Float = 0.0f,
	block: MutableList<SplinePoint>.() -> Unit = {},
) = apply { add(SplinePoint(location, SplineValue(spline = CubicSpline(coordinate, buildList(block))), derivative)) }
