package io.github.ayfri.kore.arguments.maths

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.pos
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.DataArgument
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Vec3(val x: PosNumber, val y: PosNumber, val z: PosNumber) : Argument, ContainerArgument, DataArgument {
	constructor(x: Number = 0, y: Number = 0, z: Number = 0) : this(x.pos, y.pos, z.pos)

	val array get() = doubleArrayOf(x.value, y.value, z.value)
	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = x.value * x.value + y.value * y.value + z.value * z.value
	val manhattanLength get() = x.value + y.value + z.value
	val values get() = listOf(x, y, z)

	val local get() = Vec3(x.local, y.local, z.local)
	val relative get() = Vec3(x.relative, y.relative, z.relative)
	val world get() = Vec3(x.world, y.world, z.world)

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

	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> z
	}

	fun set(axis: Axis, value: Number) = when (axis) {
		Axis.X -> Vec3(value.pos, y, z)
		Axis.Y -> Vec3(x, value.pos, z)
		Axis.Z -> Vec3(x, y, value.pos)
	}

	fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> Vec3(value, y, z)
		Axis.Y -> Vec3(x, value, z)
		Axis.Z -> Vec3(x, y, value)
	}

	/**
	 * Returns the angle between this vector and [other] in radians.
	 */
	infix fun angleTo(other: Vec3): Double {
		val len1 = length
		val len2 = other.length

		if (len1 == 0.0 || len2 == 0.0) return 0.0

		val dot = this dot other
		val lengthProduct = len1 * len2
		return acos((dot.value / lengthProduct).coerceIn(-1.0, 1.0))
	}

	infix fun cross(other: Vec3) = Vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
	infix fun distanceTo(other: Vec3) = (this - other).length
	infix fun distanceSquaredTo(other: Vec3) = (this - other).lengthSquared
	infix fun dot(other: Vec3) = x * other.x + y * other.y + z * other.z
	infix fun manhattanDistanceTo(other: Vec3) = (this - other).manhattanLength
	infix fun max(other: Vec3) = Vec3(maxOf(x, other.x), maxOf(y, other.y), maxOf(z, other.z))
	infix fun min(other: Vec3) = Vec3(minOf(x, other.x), minOf(y, other.y), minOf(z, other.z))

	fun abs() = Vec3(+x, +y, +z)
	fun ceil() = Vec3(kotlin.math.ceil(x.value), kotlin.math.ceil(y.value), kotlin.math.ceil(z.value))
	fun floor() = Vec3(kotlin.math.floor(x.value), kotlin.math.floor(y.value), kotlin.math.floor(z.value))
	fun negate() = Vec3(-x, -y, -z)
	fun normalize() = this / length
	fun round() = Vec3(x.value.roundToInt(), y.value.roundToInt(), z.value.roundToInt())

	fun toStringValues() = "${x.value} ${y.value} ${z.value}"
	fun toVec2() = Vec2(x, y)
}

fun vec3(x: Number, y: Number, z: Number) = Vec3(x, y, z)
fun vec3(x: PosNumber, y: PosNumber, z: PosNumber) = Vec3(x, y, z)
fun vec3(x: PosNumber.Type, y: PosNumber.Type, z: PosNumber.Type) = Vec3(pos(type = x), pos(type = y), pos(type = z))
fun vec3(type: PosNumber.Type = PosNumber.Type.RELATIVE) = Vec3(pos(type = type), pos(type = type), pos(type = type))
