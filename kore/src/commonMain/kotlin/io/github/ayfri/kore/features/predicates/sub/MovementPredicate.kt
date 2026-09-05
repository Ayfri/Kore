package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

/**
 * Matches the velocity and fall distance of an entity, keyed under `minecraft:movement`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class MovementPredicate(
	var x: FloatRangeOrFloatJson? = null,
	var y: FloatRangeOrFloatJson? = null,
	var z: FloatRangeOrFloatJson? = null,
	var speed: FloatRangeOrFloatJson? = null,
	var horizontalSpeed: FloatRangeOrFloatJson? = null,
	var verticalSpeed: FloatRangeOrFloatJson? = null,
	var fallDistance: FloatRangeOrFloatJson? = null,
)

/** Creates a [MovementPredicate]. */
fun movementPredicate(init: MovementPredicate.() -> Unit = {}) = MovementPredicate().apply(init)

/** Matches an exact velocity on the X axis. */
fun MovementPredicate.x(value: Number) {
	x = rangeOrDouble(value.toDouble())
}

/** Matches a velocity on the X axis between [min] and [max]. */
fun MovementPredicate.x(min: Number, max: Number) {
	x = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a velocity on the X axis within [range]. */
fun <T> MovementPredicate.x(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	x = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact velocity on the Y axis. */
fun MovementPredicate.y(value: Number) {
	y = rangeOrDouble(value.toDouble())
}

/** Matches a velocity on the Y axis between [min] and [max]. */
fun MovementPredicate.y(min: Number, max: Number) {
	y = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a velocity on the Y axis within [range]. */
fun <T> MovementPredicate.y(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	y = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact velocity on the Z axis. */
fun MovementPredicate.z(value: Number) {
	z = rangeOrDouble(value.toDouble())
}

/** Matches a velocity on the Z axis between [min] and [max]. */
fun MovementPredicate.z(min: Number, max: Number) {
	z = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a velocity on the Z axis within [range]. */
fun <T> MovementPredicate.z(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	z = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact speed. */
fun MovementPredicate.speed(value: Number) {
	speed = rangeOrDouble(value.toDouble())
}

/** Matches a speed between [min] and [max]. */
fun MovementPredicate.speed(min: Number, max: Number) {
	speed = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a speed within [range]. */
fun <T> MovementPredicate.speed(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	speed = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact horizontal speed. */
fun MovementPredicate.horizontalSpeed(value: Number) {
	horizontalSpeed = rangeOrDouble(value.toDouble())
}

/** Matches a horizontal speed between [min] and [max]. */
fun MovementPredicate.horizontalSpeed(min: Number, max: Number) {
	horizontalSpeed = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a horizontal speed within [range]. */
fun <T> MovementPredicate.horizontalSpeed(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	horizontalSpeed = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact vertical speed. */
fun MovementPredicate.verticalSpeed(value: Number) {
	verticalSpeed = rangeOrDouble(value.toDouble())
}

/** Matches a vertical speed between [min] and [max]. */
fun MovementPredicate.verticalSpeed(min: Number, max: Number) {
	verticalSpeed = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a vertical speed within [range]. */
fun <T> MovementPredicate.verticalSpeed(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	verticalSpeed = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}

/** Matches an exact fall distance. */
fun MovementPredicate.fallDistance(value: Number) {
	fallDistance = rangeOrDouble(value.toDouble())
}

/** Matches a fall distance between [min] and [max]. */
fun MovementPredicate.fallDistance(min: Number, max: Number) {
	fallDistance = rangeOrDouble(min.toDouble(), max.toDouble())
}

/** Matches a fall distance within [range]. */
fun <T> MovementPredicate.fallDistance(range: ClosedRange<T>) where T : Number, T : Comparable<T> {
	fallDistance = rangeOrDouble(range.start.toDouble(), range.endInclusive.toDouble())
}
