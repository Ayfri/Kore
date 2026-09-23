package io.github.ayfri.kore.arguments.maths

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.enums.Axis
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.numbers.pos
import io.github.ayfri.kore.arguments.numbers.toStringWithDecimal
import io.github.ayfri.kore.arguments.types.ContainerArgument
import io.github.ayfri.kore.arguments.types.DataArgument
import kotlinx.serialization.Serializable
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Three [PosNumber] components in command order: x, y, z. Used for positions, motion vectors, and other triples in
 * Minecraft command syntax. The maths below work on the raw values, the result keeps the left operand's
 * coordinate type (`~`, `^` or world).
 *
 * ```kotlin
 * vec3(1, 2, 3) + vec3(1, 0, 0) // 2.0 2.0 3.0
 * vec3(0, 0, 1).local // ^ ^ ^1
 * ```
 */
@Serializable
data class Vec3(
	/** X axis component. */
	val x: PosNumber,
	/** Y axis component. */
	val y: PosNumber,
	/** Z axis component. */
	val z: PosNumber,
) : Argument, ContainerArgument, DataArgument {
	constructor(x: Number = 0, y: Number = 0, z: Number = 0) : this(x.pos, y.pos, z.pos)

	val array get() = doubleArrayOf(x.value, y.value, z.value)
	val length get() = sqrt(lengthSquared)
	val lengthSquared get() = this dot this

	/** Sum of the absolute components, the number of block steps along the axes. */
	val manhattanLength get() = x.abs().value + y.abs().value + z.abs().value
	val values get() = listOf(x, y, z)

	val local get() = Vec3(x.local, y.local, z.local)
	val relative get() = Vec3(x.relative, y.relative, z.relative)
	val world get() = Vec3(x.world, y.world, z.world)

	val isLocal get() = x.isLocal && y.isLocal && z.isLocal
	val isRelative get() = x.isRelative && y.isRelative && z.isRelative
	val isWorld get() = x.isWorld && y.isWorld && z.isWorld

	operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
	operator fun plus(value: Number) = Vec3(x + value, y + value, z + value)
	operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
	operator fun minus(value: Number) = Vec3(x - value, y - value, z - value)
	operator fun times(other: Vec3) = Vec3(x * other.x, y * other.y, z * other.z)
	operator fun times(factor: Number) = Vec3(x * factor, y * factor, z * factor)
	operator fun div(other: Vec3) = Vec3(x / other.x, y / other.y, z / other.z)
	operator fun div(divisor: Number) = Vec3(x / divisor, y / divisor, z / divisor)
	operator fun rem(other: Vec3) = Vec3(x % other.x, y % other.y, z % other.z)
	operator fun rem(divisor: Number) = Vec3(x % divisor, y % divisor, z % divisor)
	operator fun unaryMinus() = Vec3(-x, -y, -z)

	override fun asString() = "$x $y $z"

	operator fun get(axis: Axis) = when (axis) {
		Axis.X -> x
		Axis.Y -> y
		Axis.Z -> z
	}

	fun set(axis: Axis, value: Number) = set(axis, PosNumber(value.toDouble(), this[axis].type))

	fun set(axis: Axis, value: PosNumber) = when (axis) {
		Axis.X -> copy(x = value)
		Axis.Y -> copy(y = value)
		Axis.Z -> copy(z = value)
	}

	/** Angle between this vector and [other] in radians, `0` if either is a zero vector. */
	infix fun angleTo(other: Vec3): Double {
		val lengthProduct = length * other.length
		return if (lengthProduct == 0.0) 0.0 else acos(((this dot other) / lengthProduct).coerceIn(-1.0, 1.0))
	}

	infix fun cross(other: Vec3) = Vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
	infix fun distanceTo(other: Vec3) = (this - other).length
	infix fun distanceSquaredTo(other: Vec3) = (this - other).lengthSquared
	infix fun dot(other: Vec3) = x.value * other.x.value + y.value * other.y.value + z.value * other.z.value
	infix fun manhattanDistanceTo(other: Vec3) = (this - other).manhattanLength
	infix fun max(other: Vec3) = Vec3(maxOf(x, other.x), maxOf(y, other.y), maxOf(z, other.z))
	infix fun min(other: Vec3) = Vec3(minOf(x, other.x), minOf(y, other.y), minOf(z, other.z))

	/** Linear interpolation, `t = 0` gives this vector and `t = 1` gives [other]. */
	fun lerp(other: Vec3, t: Double) = this + (other - this) * t

	fun abs() = Vec3(x.abs(), y.abs(), z.abs())
	fun ceil() = Vec3(kotlin.math.ceil(x.value), kotlin.math.ceil(y.value), kotlin.math.ceil(z.value))
	fun floor() = Vec3(kotlin.math.floor(x.value), kotlin.math.floor(y.value), kotlin.math.floor(z.value))

	/** This vector scaled to a length of `1`, a zero vector stays zero. */
	fun normalize() = length.let { if (it == 0.0) this else this / it }
	fun round() = Vec3(x.value.roundToInt(), y.value.roundToInt(), z.value.roundToInt())
	fun truncate() = Vec3(x.truncate(), y.truncate(), z.truncate())

	fun toStringTruncatedIfZero() = "${x.toStringTruncatedIfZero()} ${y.toStringTruncatedIfZero()} ${z.toStringTruncatedIfZero()}"

	/** Block-position form, world components are floored like Minecraft does. */
	fun toStringTruncated() = "${x.toStringTruncated()} ${y.toStringTruncated()} ${z.toStringTruncated()}"
	fun toStringValues() = "${x.value.toStringWithDecimal} ${y.value.toStringWithDecimal} ${z.value.toStringWithDecimal}"

	/** Drops the Y component, `(x, y, z)` becomes the column position `(x, z)`. */
	fun toVec2() = Vec2(x, z)

	companion object {
		fun fromString(string: String) = string.split(' ').let {
			require(it.size == 3) { "Vec3 string must have exactly 3 components, got '$string'." }
			Vec3(it[0].toDouble(), it[1].toDouble(), it[2].toDouble())
		}
	}
}

fun vec3(x: Number, y: Number, z: Number) = Vec3(x, y, z)
fun vec3(x: PosNumber, y: PosNumber, z: PosNumber) = Vec3(x, y, z)
fun vec3(x: PosNumber.Type, y: PosNumber.Type, z: PosNumber.Type) = Vec3(pos(type = x), pos(type = y), pos(type = z))
fun vec3(type: PosNumber.Type = PosNumber.Type.RELATIVE) = Vec3(pos(type = type), pos(type = type), pos(type = type))
