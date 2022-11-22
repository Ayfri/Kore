package arguments

import arguments.enums.Axis
import arguments.numbers.PosNumber
import arguments.numbers.pos

data class Coordinate(val x: PosNumber, val y: PosNumber, val z: PosNumber) : Argument, Argument.Data {
	constructor(x: Number, y: Number, z: Number) : this(x.pos, y.pos, z.pos)
	
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
}
