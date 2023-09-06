package arguments.maths

import arguments.Argument
import arguments.enums.Axis
import arguments.numbers.PosNumber
import arguments.numbers.pos
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Vec2(val x: PosNumber, val y: PosNumber) : Argument {
	constructor(x: Number = 0, y: Number = 0) : this(x.pos, y.pos)

	val array get() = doubleArrayOf(x.value, y.value)
	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = x.value * x.value + y.value * y.value
	val manhattanLength get() = x.value + y.value
	val values get() = listOf(x, y)

	val local get() = Vec2(x.local, y.local)
	val relative get() = Vec2(x.relative, y.relative)
	val world get() = Vec2(x.world, y.world)

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

	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> error("Z component doesn't exists for Vec2.")
	}

	fun set(axis: Axis, value: Number) = when (axis) {
		Axis.X -> Vec2(value.pos, y)
		Axis.Y -> Vec2(x, value.pos)
		else -> Vec2(x, y)
	}

	fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> Vec2(value, y)
		Axis.Y -> Vec2(x, value)
		Axis.Z -> Vec2(x, y)
	}

	/**
	 * Returns the angle between this vector and [other] in radians.
	 */
	infix fun angleTo(other: Vec2): Double {
		val len1 = length
		val len2 = other.length

		if (len1 == 0.0 || len2 == 0.0) return 0.0

		val dot = this dot other
		val lengthProduct = len1 * len2
		return acos((dot.value / lengthProduct).coerceIn(-1.0, 1.0))
	}

	infix fun cross(other: Vec2) = Vec2(x * other.y, y * other.x)
	infix fun distanceSquaredTo(other: Vec2) = (this - other).lengthSquared
	infix fun distanceTo(other: Vec2) = (this - other).length
	infix fun dot(other: Vec2) = x * other.x + y * other.y
	infix fun manhattanDistanceTo(other: Vec2) = (this - other).manhattanLength
	infix fun max(other: Vec2) = Vec2(maxOf(x, other.x), maxOf(y, other.y))
	infix fun min(other: Vec2) = Vec2(minOf(x, other.x), minOf(y, other.y))

	fun abs() = Vec2(+x, +y)
	fun ceil() = Vec2(kotlin.math.ceil(x.value), kotlin.math.ceil(y.value))
	fun floor() = Vec2(kotlin.math.floor(x.value), kotlin.math.floor(y.value))
	fun negate() = Vec2(-x, -y)
	fun normalize() = this / length
	fun round() = Vec2(x.value.roundToInt(), y.value.roundToInt())

	fun toStringValues() = "${x.value} ${y.value}"
	fun toVec3(z: PosNumber) = Vec3(x, y, z)
	fun toVec3(z: Number = 0, type: PosNumber.Type = PosNumber.Type.WORLD) = Vec3(x, y, pos(z, type))
}

fun vec2(x: Number, y: Number) = Vec2(x, y)
fun vec2(x: PosNumber, y: PosNumber) = Vec2(x, y)
fun vec2(x: PosNumber.Type, y: PosNumber.Type) = Vec2(pos(type = x), pos(type = y))
fun vec2(type: PosNumber.Type = PosNumber.Type.RELATIVE) = Vec2(pos(type = type), pos(type = type))
