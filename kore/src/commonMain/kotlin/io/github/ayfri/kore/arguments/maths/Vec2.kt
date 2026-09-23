package io.github.ayfri.kore.arguments.maths

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.pos
import io.github.ayfri.kore.arguments.numbers.toStringWithDecimal
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Two [PosNumber] components, used by Minecraft for column positions (`forceload`, `spreadplayers`, `worldborder`),
 * so the second component is the world Z axis even though it is named [y].
 *
 * ```kotlin
 * vec2(3, -5).toVec3(64.0) // 3.0 64.0 -5.0
 * ```
 */
data class Vec2(val x: PosNumber, val y: PosNumber) : Argument {
	constructor(x: Number = 0, y: Number = 0) : this(x.pos, y.pos)

	val array get() = doubleArrayOf(x.value, y.value)
	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = this dot this

	/** Sum of the absolute components, the number of block steps along the axes. */
	val manhattanLength get() = x.abs().value + y.abs().value
	val values get() = listOf(x, y)

	val local get() = Vec2(x.local, y.local)
	val relative get() = Vec2(x.relative, y.relative)
	val world get() = Vec2(x.world, y.world)

	operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
	operator fun plus(value: Number) = Vec2(x + value, y + value)
	operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
	operator fun minus(value: Number) = Vec2(x - value, y - value)
	operator fun times(other: Vec2) = Vec2(x * other.x, y * other.y)
	operator fun times(factor: Number) = Vec2(x * factor, y * factor)
	operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)
	operator fun div(divisor: Number) = Vec2(x / divisor, y / divisor)
	operator fun rem(other: Vec2) = Vec2(x % other.x, y % other.y)
	operator fun rem(divisor: Number) = Vec2(x % divisor, y % divisor)
	operator fun unaryMinus() = Vec2(-x, -y)

	override fun asString() = "$x $y"

	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> error("Vec2 has no Z component.")
	}

	fun set(axis: Axis, value: Number) = set(axis, PosNumber(value.toDouble(), this[axis].type))

	fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> copy(x = value)
		Axis.Y -> copy(y = value)
		Axis.Z -> error("Vec2 has no Z component.")
	}

	/** Angle between this vector and [other] in radians, `0` if either is a zero vector. */
	infix fun angleTo(other: Vec2): Double {
		val lengthProduct = length * other.length
		return if (lengthProduct == 0.0) 0.0 else acos(((this dot other) / lengthProduct).coerceIn(-1.0, 1.0))
	}

	/** 2D cross product `x × other.y - y × other.x`, positive when [other] is counter-clockwise from this vector. */
	infix fun cross(other: Vec2) = x.value * other.y.value - y.value * other.x.value
	infix fun distanceSquaredTo(other: Vec2) = (this - other).lengthSquared
	infix fun distanceTo(other: Vec2) = (this - other).length
	infix fun dot(other: Vec2) = x.value * other.x.value + y.value * other.y.value
	infix fun manhattanDistanceTo(other: Vec2) = (this - other).manhattanLength
	infix fun max(other: Vec2) = Vec2(maxOf(x, other.x), maxOf(y, other.y))
	infix fun min(other: Vec2) = Vec2(minOf(x, other.x), minOf(y, other.y))

	/** Linear interpolation, `t = 0` gives this vector and `t = 1` gives [other]. */
	fun lerp(other: Vec2, t: Double) = this + (other - this) * t

	fun abs() = Vec2(x.abs(), y.abs())
	fun ceil() = Vec2(kotlin.math.ceil(x.value), kotlin.math.ceil(y.value))
	fun floor() = Vec2(kotlin.math.floor(x.value), kotlin.math.floor(y.value))

	/** This vector scaled to a length of `1`, a zero vector stays zero. */
	fun normalize() = length.let { if (it == 0.0) this else this / it }
	fun round() = Vec2(x.value.roundToInt(), y.value.roundToInt())
	fun truncate() = Vec2(x.truncate(), y.truncate())

	fun toStringTruncatedIfZero() = "${x.toStringTruncatedIfZero()} ${y.toStringTruncatedIfZero()}"

	/** Block-position form, world components are floored like Minecraft does. */
	fun toStringTruncated() = "${x.toStringTruncated()} ${y.toStringTruncated()}"
	fun toStringValues() = "${x.value.toStringWithDecimal} ${y.value.toStringWithDecimal}"

	/** Inserts [y] as the height, the column `(x, z)` becomes `(x, y, z)`. */
	fun toVec3(y: PosNumber) = Vec3(x, y, this.y)
	fun toVec3(y: Number = 0, type: PosNumber.Type = PosNumber.Type.WORLD) = toVec3(pos(y, type))

	companion object {
		fun fromString(string: String) = string.split(' ').let {
			require(it.size == 2) { "Vec2 string must have exactly 2 components, got '$string'." }
			Vec2(it[0].toDouble(), it[1].toDouble())
		}
	}
}

fun vec2(x: Number, y: Number) = Vec2(x, y)
fun vec2(x: PosNumber, y: PosNumber) = Vec2(x, y)
fun vec2(x: PosNumber.Type, y: PosNumber.Type) = Vec2(pos(type = x), pos(type = y))
fun vec2(type: PosNumber.Type = PosNumber.Type.RELATIVE) = Vec2(pos(type = type), pos(type = type))
