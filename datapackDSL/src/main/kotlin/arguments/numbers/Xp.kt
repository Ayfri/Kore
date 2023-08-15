package arguments.numbers

import arguments.enums.ExperienceType
import arguments.selector.json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

data class Xp(val value: Long, val type: ExperienceType = ExperienceType.POINTS) : Comparable<Xp> {
	val typeString get() = json.encodeToJsonElement(type).jsonPrimitive.content

	operator fun plus(other: Xp) = Xp(value + other.value, type)
	operator fun plus(other: Number) = Xp(value + other.toLong(), type)
	operator fun minus(other: Xp) = Xp(value - other.value, type)
	operator fun minus(other: Number) = Xp(value - other.toLong(), type)
	operator fun times(other: Xp) = Xp(value * other.value, type)
	operator fun times(other: Number) = Xp(value * other.toLong(), type)
	operator fun div(other: Xp) = Xp(value / other.value, type)
	operator fun div(other: Number) = Xp(value / other.toLong(), type)
	operator fun rem(other: Xp) = Xp(value % other.value, type)
	operator fun rem(other: Number) = Xp(value % other.toLong(), type)
	operator fun unaryMinus() = Xp(-value, type)
	operator fun unaryPlus() = Xp(+value, type)
	override fun compareTo(other: Xp) = value.compareTo(other.value)

	fun toLevels() = Xp(value, ExperienceType.LEVELS)
	fun toPoints() = Xp(value, ExperienceType.POINTS)

	override fun toString() = when (type) {
		ExperienceType.LEVELS -> "$value levels"
		ExperienceType.POINTS -> value.toString()
	}
}

val Number.levels get() = Xp(toLong(), ExperienceType.LEVELS)
val Number.points get() = Xp(toLong(), ExperienceType.POINTS)
