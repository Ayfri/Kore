package arguments

import arguments.enums.Axis
import arguments.numbers.PosNumber
import arguments.numbers.pos
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Vec2(val x: PosNumber, val y: PosNumber) : Argument {
	constructor(x: Number = 0, y: Number = 0) : this(x.pos, y.pos)

	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = x.value * x.value + y.value * y.value
	val manhattanLength get() = x.value + y.value

	operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
	operator fun plus(quotient: Number) = Vec2(x + quotient, y + quotient)
	operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
	operator fun minus(quotient: Number) = Vec2(x - quotient, y - quotient)
	operator fun times(other: Vec2) = Vec2(x * other.x, y * other.y)
	operator fun times(quotient: Number) = Vec2(x * quotient, y * quotient)
	operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)
	operator fun div(quotient: Number) = Vec2(x / quotient, y / quotient)
	operator fun rem(other: Vec2) = Vec2(x % other.x, y % other.y)
	operator fun rem(quotient: Number) = Vec2(x % quotient, y % quotient)
	operator fun unaryMinus() = Vec2(-x, -y)
	operator fun unaryPlus() = Vec2(+x, +y)

	override fun asString() = "$x $y"

	operator fun set(axis: Axis, value: Number) = when (axis) {
		Axis.X -> Vec2(value.pos, y)
		Axis.Y -> Vec2(x, value.pos)
		else -> Vec2(x, y)
	}

	operator fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> Vec2(value, y)
		Axis.Y -> Vec2(x, value)
		Axis.Z -> Vec2(x, y)
	}

	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> error("Z component doesn't exists for Vec2.")
	}

	infix fun distanceTo(other: Vec2) = (this - other).length
	infix fun distanceSquaredTo(other: Vec2) = (this - other).lengthSquared
	infix fun manhattanDistanceTo(other: Vec2) = (this - other).manhattanLength
	infix fun dot(other: Vec2) = x * other.x + y * other.y
	infix fun angleTo(other: Vec2) = acos((this dot other / (this.length * other.length)).value)
	infix fun min(other: Vec2) = Vec2(minOf(x, other.x), minOf(y, other.y))
	infix fun max(other: Vec2) = Vec2(maxOf(x, other.x), maxOf(y, other.y))

	fun round() = Vec2(x.value.roundToInt(), y.value.roundToInt())
	fun floor() = Vec2(kotlin.math.floor(x.value), kotlin.math.floor(y.value))
	fun ceil() = Vec2(kotlin.math.ceil(x.value), kotlin.math.ceil(y.value))
	fun abs() = Vec2(+x, +y)
	fun negate() = Vec2(-x, -y)
	fun normalize() = this / length
}
