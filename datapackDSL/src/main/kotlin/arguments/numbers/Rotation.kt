package arguments.numbers

import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

@Serializable(with = RotNumber.Companion.RotNumberSerializer::class)
data class RotNumber(val value: Double, val type: Type = Type.WORLD) : Comparable<RotNumber> {
	enum class Type {
		RELATIVE,
		WORLD
	}

	operator fun plus(other: RotNumber) = RotNumber(value + other.value, type)
	operator fun plus(other: Number) = RotNumber(value + other.toDouble(), type)
	operator fun minus(other: RotNumber) = RotNumber(value - other.value, type)
	operator fun minus(other: Number) = RotNumber(value - other.toDouble(), type)
	operator fun times(other: RotNumber) = RotNumber(value * other.value, type)
	operator fun times(other: Number) = RotNumber(value * other.toDouble(), type)
	operator fun div(other: RotNumber) = RotNumber(value / other.value, type)
	operator fun div(other: Number) = RotNumber(value / other.toDouble(), type)
	operator fun rem(other: RotNumber) = RotNumber(value % other.value, type)
	operator fun rem(other: Number) = RotNumber(value % other.toDouble(), type)
	operator fun unaryMinus() = RotNumber(-value, type)
	operator fun unaryPlus() = RotNumber(+value, type)
	override fun compareTo(other: RotNumber) = value.compareTo(other.value)

	fun toRelative() = RotNumber(value, Type.RELATIVE)
	fun toWorld() = RotNumber(value, Type.WORLD)

	override fun toString() = when (type) {
		Type.RELATIVE -> "~${value.strUnlessZero}"
		Type.WORLD -> value.str
	}

	companion object {
		data object RotNumberSerializer : ToStringSerializer<RotNumber>()
	}
}

val Number.rot get() = RotNumber(toDouble())
val Number.relativeRot get() = RotNumber(toDouble(), RotNumber.Type.RELATIVE)
fun rot(value: Number = 0, type: RotNumber.Type = RotNumber.Type.RELATIVE) = RotNumber(value.toDouble(), type)
