package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

/**
 * Matches the distance between the entity and the origin of the loot context, keyed under `minecraft:distance`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class DistancePredicate(
	/** Euclidean distance on all three axes. */
	var absolute: FloatRangeOrFloatJson? = null,
	/** Euclidean distance on the X and Z axes only. */
	var horizontal: FloatRangeOrFloatJson? = null,
	var x: FloatRangeOrFloatJson? = null,
	var y: FloatRangeOrFloatJson? = null,
	var z: FloatRangeOrFloatJson? = null,
)

/** Creates a [DistancePredicate]. */
fun distancePredicate(init: DistancePredicate.() -> Unit = {}) = DistancePredicate().apply(init)

/** Matches an exact absolute distance. */
fun DistancePredicate.absolute(value: Number) {
	absolute = rangeOrDouble(value.toDouble())
}

/** Matches an absolute distance between [min] and [max]. */
fun DistancePredicate.absolute(min: Number, max: Number) {
	absolute = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches an absolute distance within [range]. */
fun <T> DistancePredicate.absolute(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	absolute = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact horizontal distance. */
fun DistancePredicate.horizontal(value: Number) {
	horizontal = rangeOrDouble(value.toDouble())
}

/** Matches a horizontal distance between [min] and [max]. */
fun DistancePredicate.horizontal(min: Number, max: Number) {
	horizontal = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a horizontal distance within [range]. */
fun <T> DistancePredicate.horizontal(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	horizontal = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact distance on the X axis. */
fun DistancePredicate.x(value: Number) {
	x = rangeOrDouble(value.toDouble())
}

/** Matches a distance on the X axis between [min] and [max]. */
fun DistancePredicate.x(min: Number, max: Number) {
	x = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a distance on the X axis within [range]. */
fun <T> DistancePredicate.x(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	x = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact distance on the Y axis. */
fun DistancePredicate.y(value: Number) {
	y = rangeOrDouble(value.toDouble())
}

/** Matches a distance on the Y axis between [min] and [max]. */
fun DistancePredicate.y(min: Number, max: Number) {
	y = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a distance on the Y axis within [range]. */
fun <T> DistancePredicate.y(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	y = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact distance on the Z axis. */
fun DistancePredicate.z(value: Number) {
	z = rangeOrDouble(value.toDouble())
}

/** Matches a distance on the Z axis between [min] and [max]. */
fun DistancePredicate.z(min: Number, max: Number) {
	z = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a distance on the Z axis within [range]. */
fun <T> DistancePredicate.z(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	z = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}
