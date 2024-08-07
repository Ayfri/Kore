package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.numbers.ranges.asRangeOrDouble
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import io.github.ayfri.kore.features.advancements.serializers.FloatRangeOrFloatJson
import kotlinx.serialization.Serializable

@Serializable
data class Movement(
	var x: FloatRangeOrFloatJson? = null,
	var y: FloatRangeOrFloatJson? = null,
	var z: FloatRangeOrFloatJson? = null,
	var speed: FloatRangeOrFloatJson? = null,
	var horizontalSpeed: FloatRangeOrFloatJson? = null,
	var verticalSpeed: FloatRangeOrFloatJson? = null,
	var fallDistance: FloatRangeOrFloatJson? = null,
)

fun movement(init: Movement.() -> Unit = {}) = Movement().apply(init)

fun Movement.x(value: Double) {
	x = rangeOrDouble(value)
}

fun Movement.x(min: Double, max: Double) {
	x = (min..max).asRangeOrDouble()
}

fun Movement.y(value: Double) {
	y = rangeOrDouble(value)
}

fun Movement.y(min: Double, max: Double) {
	y = (min..max).asRangeOrDouble()
}

fun Movement.z(min: Double, max: Double) {
	z = (min..max).asRangeOrDouble()
}

fun Movement.z(value: Double) {
	z = rangeOrDouble(value)
}

fun Movement.speed(value: Double) {
	speed = rangeOrDouble(value)
}

fun Movement.speed(min: Double, max: Double) {
	speed = (min..max).asRangeOrDouble()
}

fun Movement.horizontalSpeed(value: Double) {
	horizontalSpeed = rangeOrDouble(value)
}

fun Movement.horizontalSpeed(min: Double, max: Double) {
	horizontalSpeed = (min..max).asRangeOrDouble()
}

fun Movement.verticalSpeed(value: Double) {
	verticalSpeed = rangeOrDouble(value)
}

fun Movement.verticalSpeed(min: Double, max: Double) {
	verticalSpeed = (min..max).asRangeOrDouble()
}

fun Movement.fallDistance(value: Double) {
	fallDistance = rangeOrDouble(value)
}

fun Movement.fallDistance(min: Double, max: Double) {
	fallDistance = (min..max).asRangeOrDouble()
}
