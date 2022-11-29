package arguments

import arguments.enums.Axis
import arguments.numbers.PosNumber
import arguments.numbers.pos
import kotlin.math.acos
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Coordinate(val x: PosNumber, val y: PosNumber, val z: PosNumber) : Argument, Argument.Data {
	constructor(x: Number = 0, y: Number = 0, z: Number = 0) : this(x.pos, y.pos, z.pos)
	
	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = x.value * x.value + y.value * y.value + z.value * z.value
	val manhattanLength get() = x.value + y.value + z.value
	
	operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y, z + other.z)
	operator fun plus(quotient: Number) = Coordinate(x + quotient, y + quotient, z + quotient)
	operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y, z - other.z)
	operator fun minus(quotient: Number) = Coordinate(x - quotient, y - quotient, z - quotient)
	operator fun times(other: Coordinate) = Coordinate(x * other.x, y * other.y, z * other.z)
	operator fun times(quotient: Number) = Coordinate(x * quotient, y * quotient, z * quotient)
	operator fun div(other: Coordinate) = Coordinate(x / other.x, y / other.y, z / other.z)
	operator fun div(quotient: Number) = Coordinate(x / quotient, y / quotient, z / quotient)
	operator fun rem(other: Coordinate) = Coordinate(x % other.x, y % other.y, z % other.z)
	operator fun rem(quotient: Number) = Coordinate(x % quotient, y % quotient, z % quotient)
	operator fun unaryMinus() = Coordinate(-x, -y, -z)
	operator fun unaryPlus() = Coordinate(+x, +y, +z)
	
	override fun asString() = "$x $y $z"
	
	operator fun set(axis: Axis, value: Number) = when (axis) {
		Axis.X -> Coordinate(value.pos, y, z)
		Axis.Y -> Coordinate(x, value.pos, z)
		Axis.Z -> Coordinate(x, y, value.pos)
	}
	
	operator fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> Coordinate(value, y, z)
		Axis.Y -> Coordinate(x, value, z)
		Axis.Z -> Coordinate(x, y, value)
	}
	
	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> z
	}
	
	infix fun distanceTo(other: Coordinate) = (this - other).length
	infix fun distanceSquaredTo(other: Coordinate) = (this - other).lengthSquared
	infix fun manhattanDistanceTo(other: Coordinate) = (this - other).manhattanLength
	infix fun dot(other: Coordinate) = x * other.x + y * other.y + z * other.z
	infix fun cross(other: Coordinate) = Coordinate(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
	infix fun angleTo(other: Coordinate) = acos((this dot other / (this.length * other.length)).value)
	infix fun min(other: Coordinate) = Coordinate(minOf(x, other.x), minOf(y, other.y), minOf(z, other.z))
	infix fun max(other: Coordinate) = Coordinate(maxOf(x, other.x), maxOf(y, other.y), maxOf(z, other.z))
	
	fun round() = Coordinate(x.value.roundToInt(), y.value.roundToInt(), z.value.roundToInt())
	fun floor() = Coordinate(floor(x.value), floor(y.value), floor(z.value))
	fun ceil() = Coordinate(ceil(x.value), ceil(y.value), ceil(z.value))
	fun abs() = Coordinate(+x, +y, +z)
	fun negate() = Coordinate(-x, -y, -z)
	fun normalize() = this / length
}
