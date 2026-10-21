package io.github.ayfri.kore.features.worldgen.densityfunction.types

import io.github.ayfri.kore.features.worldgen.densityfunction.DensityFunctionsScope
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * How a [DistanceToPoint] measures the distance between the sampled position and its point, with `dx`, `dy` and `dz`
 * the differences on each axis.
 *
 * - [CHEBYSHEV]: `max(abs(dx), abs(dy), abs(dz))`.
 * - [EUCLIDEAN]: `sqrt(dx^2 + dy^2 + dz^2)`.
 * - [EUCLIDEAN_SQUARED]: `dx^2 + dy^2 + dz^2`.
 * - [MANHATTAN]: `abs(dx) + abs(dy) + abs(dz)`.
 */
@Serializable(with = DistanceMetric.Companion.DistanceMetricSerializer::class)
enum class DistanceMetric {
	CHEBYSHEV,
	EUCLIDEAN,
	EUCLIDEAN_SQUARED,
	MANHATTAN;

	companion object {
		data object DistanceMetricSerializer : LowercaseSerializer<DistanceMetric>(entries)
	}
}

/**
 * Outputs the distance between the sampled position and [point], measured with [metric].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
@Serializable
data class DistanceToPoint(
	var metric: DistanceMetric = DistanceMetric.EUCLIDEAN,
	var point: TripleAsArray<Int, Int, Int> = Triple(0, 0, 0),
) : DensityFunctionType()

/**
 * Adds a `distance_to_point` density function to the data pack, outputting the distance between the sampled position
 * and the block position ([x], [y], [z]), measured with [metric].
 *
 * ```kotlin
 * distanceToPoint("manhattan", 8, 64, -8, DistanceMetric.MANHATTAN)
 * ```
 *
 * Produces `data/<namespace>/worldgen/density_function/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen
 * Minecraft Wiki: https://minecraft.wiki/w/Density_function
 */
fun DensityFunctionsScope.distanceToPoint(
	fileName: String,
	x: Int,
	y: Int,
	z: Int,
	metric: DistanceMetric = DistanceMetric.EUCLIDEAN,
) = register(fileName, DistanceToPoint(metric, Triple(x, y, z)))
