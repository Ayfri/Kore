package io.github.ayfri.kore.arguments.numbers

import kotlin.math.absoluteValue

data class PosNumber(var value: Double, var type: Type = Type.WORLD) : Comparable<PosNumber> {
	enum class Type(val prefix: String = "") {
		LOCAL("^"),
		RELATIVE("~"),
		WORLD
	}

	val prefix get() = type.prefix

	val local get() = PosNumber(value, Type.LOCAL)
	val relative get() = PosNumber(value, Type.RELATIVE)
	val world get() = PosNumber(value, Type.WORLD)

	val isLocal get() = type == Type.LOCAL
	val isRelative get() = type == Type.RELATIVE
	val isWorld get() = type == Type.WORLD

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
		Type.LOCAL -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.RELATIVE -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.WORLD -> value.toString()
	}

	fun truncate() = copy(value = value.truncated)

	fun toStringTruncatedIfZero() = when (type) {
		Type.LOCAL -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.RELATIVE -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.WORLD -> value.toStringTruncatedIfRound()
	}

	fun toStringTruncated() = when (type) {
		Type.LOCAL -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.RELATIVE -> "$prefix${value.truncateIfRoundEmptyIfZero}"
		Type.WORLD -> value.toStringTruncated()
	}
}

val Number.localPos get() = PosNumber(toDouble(), PosNumber.Type.LOCAL)
val Number.relativePos get() = PosNumber(toDouble(), PosNumber.Type.RELATIVE)
val Number.pos get() = PosNumber(toDouble())
val Number.worldPos get() = pos

fun pos(value: Number = 0, type: PosNumber.Type = PosNumber.Type.RELATIVE) = PosNumber(value.toDouble(), type)
