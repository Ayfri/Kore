package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

/**
 * Matches the coordinates of a position, as the `position` key of a [LocationPredicate].
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class PositionPredicate(
	var x: FloatRangeOrFloatJson? = null,
	var y: FloatRangeOrFloatJson? = null,
	var z: FloatRangeOrFloatJson? = null,
)

/** Creates a [PositionPredicate]. */
fun positionPredicate(init: PositionPredicate.() -> Unit = {}) = PositionPredicate().apply(init)

/** Matches an exact X coordinate. */
fun PositionPredicate.x(value: Number) {
	x = rangeOrDouble(value.toDouble())
}

/** Matches an X coordinate between [min] and [max]. */
fun PositionPredicate.x(min: Number, max: Number) {
	x = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches an X coordinate within [range]. */
fun <T> PositionPredicate.x(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	x = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact Y coordinate. */
fun PositionPredicate.y(value: Number) {
	y = rangeOrDouble(value.toDouble())
}

/** Matches a Y coordinate between [min] and [max]. */
fun PositionPredicate.y(min: Number, max: Number) {
	y = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a Y coordinate within [range]. */
fun <T> PositionPredicate.y(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	y = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact Z coordinate. */
fun PositionPredicate.z(value: Number) {
	z = rangeOrDouble(value.toDouble())
}

/** Matches a Z coordinate between [min] and [max]. */
fun PositionPredicate.z(min: Number, max: Number) {
	z = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a Z coordinate within [range]. */
fun <T> PositionPredicate.z(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	z = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}
