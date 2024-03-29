package io.github.ayfri.kore.features.advancements.types

import kotlinx.serialization.Serializable

@Serializable
data class DistanceEntry(
	var min: Float? = null,
	var max: Float? = null,
)

@Serializable
data class Distance(
	var absolute: DistanceEntry? = null,
	var horizontal: DistanceEntry? = null,
	var x: DistanceEntry? = null,
	var y: DistanceEntry? = null,
	var z: DistanceEntry? = null,
)

fun distance(init: Distance.() -> Unit = {}) = Distance().apply(init)

fun Distance.absolute(min: Float? = null, max: Float? = null) {
	absolute = DistanceEntry(min, max)
}

fun Distance.absolute(value: Float) {
	absolute = DistanceEntry(value, value)
}

fun <T> Distance.absolute(value: ClosedRange<T>) where T : Number, T : Comparable<T> {
	absolute = DistanceEntry(value.start.toFloat(), value.endInclusive.toFloat())
}

fun Distance.horizontal(min: Float? = null, max: Float? = null) {
	horizontal = DistanceEntry(min, max)
}

fun Distance.horizontal(value: Float) {
	horizontal = DistanceEntry(value, value)
}

fun <T> Distance.horizontal(value: ClosedRange<T>) where T : Number, T : Comparable<T> {
	horizontal = DistanceEntry(value.start.toFloat(), value.endInclusive.toFloat())
}

fun Distance.x(min: Float, max: Float) {
	x = DistanceEntry(min, max)
}

fun Distance.x(value: Float) {
	x = DistanceEntry(value, value)
}

fun <T> Distance.x(value: ClosedRange<T>) where T : Number, T : Comparable<T> {
	x = DistanceEntry(value.start.toFloat(), value.endInclusive.toFloat())
}

fun Distance.y(min: Float, max: Float) {
	y = DistanceEntry(min, max)
}

fun Distance.y(value: Float) {
	y = DistanceEntry(value, value)
}

fun <T> Distance.y(value: ClosedRange<T>) where T : Number, T : Comparable<T> {
	y = DistanceEntry(value.start.toFloat(), value.endInclusive.toFloat())
}

fun Distance.z(min: Float, max: Float) {
	z = DistanceEntry(min, max)
}

fun Distance.z(value: Float) {
	z = DistanceEntry(value, value)
}

fun <T> Distance.z(value: ClosedRange<T>) where T : Number, T : Comparable<T> {
	z = DistanceEntry(value.start.toFloat(), value.endInclusive.toFloat())
}
