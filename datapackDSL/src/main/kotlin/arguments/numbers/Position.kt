package arguments.numbers

import kotlin.math.absoluteValue

data class PosNumber(var value: Double, var type: Type = Type.WORLD) : Comparable<PosNumber> {
	enum class Type {
		LOCAL,
		RELATIVE,
		WORLD
	}

	val local get() = PosNumber(value, Type.LOCAL)
	val relative get() = PosNumber(value, Type.RELATIVE)
	val world get() = PosNumber(value, Type.WORLD)

	operator fun plus(other: PosNumber) = PosNumber(value + other.value, type)
	operator fun plus(other: Number) = PosNumber(value + other.toDouble(), type)
	operator fun minus(other: PosNumber) = PosNumber(value - other.value, type)
	operator fun minus(other: Number) = PosNumber(value - other.toDouble(), type)
	operator fun times(other: PosNumber) = PosNumber(value * other.value, type)
	operator fun times(other: Number) = PosNumber(value * other.toDouble(), type)
	operator fun div(other: PosNumber) = PosNumber(value / other.value, type)
	operator fun div(other: Number) = PosNumber(value / other.toDouble(), type)
	operator fun rem(other: PosNumber) = PosNumber(value % other.value, type)
	operator fun rem(other: Number) = PosNumber(value % other.toDouble(), type)
	operator fun unaryMinus() = PosNumber(-value, type)
	operator fun unaryPlus() = PosNumber(value.absoluteValue, type)

	override fun compareTo(other: PosNumber) = value.compareTo(other.value)

	override fun toString() = when (type) {
		Type.RELATIVE -> "~${value.strUnlessZero}"
		Type.LOCAL -> "^${value.strUnlessZero}"
		Type.WORLD -> value.str
	}
}

val Number.localPos get() = PosNumber(toDouble(), PosNumber.Type.LOCAL)
val Number.relativePos get() = PosNumber(toDouble(), PosNumber.Type.RELATIVE)
val Number.pos get() = PosNumber(toDouble())
val Number.worldPos get() = PosNumber(toDouble())

fun pos(value: Number = 0, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type)
