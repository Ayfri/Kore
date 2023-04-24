package arguments

import arguments.enums.Axis
import arguments.numbers.PosNumber
import arguments.numbers.pos
import kotlin.math.*

typealias Coordinate = Vec3

data class Vec3(val x: PosNumber, val y: PosNumber, val z: PosNumber) : Argument, Argument.Container, Argument.Data {
	constructor(x: Number = 0, y: Number = 0, z: Number = 0) : this(x.pos, y.pos, z.pos)

	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = x.value * x.value + y.value * y.value + z.value * z.value
	val manhattanLength get() = x.value + y.value + z.value

	operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
	operator fun plus(quotient: Number) = Vec3(x + quotient, y + quotient, z + quotient)
	operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
	operator fun minus(quotient: Number) = Vec3(x - quotient, y - quotient, z - quotient)
	operator fun times(other: Vec3) = Vec3(x * other.x, y * other.y, z * other.z)
	operator fun times(quotient: Number) = Vec3(x * quotient, y * quotient, z * quotient)
	operator fun div(other: Vec3) = Vec3(x / other.x, y / other.y, z / other.z)
	operator fun div(quotient: Number) = Vec3(x / quotient, y / quotient, z / quotient)
	operator fun rem(other: Vec3) = Vec3(x % other.x, y % other.y, z % other.z)
	operator fun rem(quotient: Number) = Vec3(x % quotient, y % quotient, z % quotient)
	operator fun unaryMinus() = Vec3(-x, -y, -z)
	operator fun unaryPlus() = Vec3(+x, +y, +z)

	override fun asString() = "$x $y $z"

	operator fun set(axis: Axis, value: Number) = when (axis) {
		Axis.X -> Vec3(value.pos, y, z)
		Axis.Y -> Vec3(x, value.pos, z)
		Axis.Z -> Vec3(x, y, value.pos)
	}

	operator fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> Vec3(value, y, z)
		Axis.Y -> Vec3(x, value, z)
		Axis.Z -> Vec3(x, y, value)
	}

	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> z
	}

	infix fun distanceTo(other: Vec3) = (this - other).length
	infix fun distanceSquaredTo(other: Vec3) = (this - other).lengthSquared
	infix fun manhattanDistanceTo(other: Vec3) = (this - other).manhattanLength
	infix fun dot(other: Vec3) = x * other.x + y * other.y + z * other.z
	infix fun cross(other: Vec3) = Vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
	infix fun angleTo(other: Vec3) = acos((this dot other / (this.length * other.length)).value)
	infix fun min(other: Vec3) = Vec3(minOf(x, other.x), minOf(y, other.y), minOf(z, other.z))
	infix fun max(other: Vec3) = Vec3(maxOf(x, other.x), maxOf(y, other.y), maxOf(z, other.z))

	fun round() = Vec3(x.value.roundToInt(), y.value.roundToInt(), z.value.roundToInt())
	fun floor() = Vec3(floor(x.value), floor(y.value), floor(z.value))
	fun ceil() = Vec3(ceil(x.value), ceil(y.value), ceil(z.value))
	fun abs() = Vec3(+x, +y, +z)
	fun negate() = Vec3(-x, -y, -z)
	fun normalize() = this / length
}
