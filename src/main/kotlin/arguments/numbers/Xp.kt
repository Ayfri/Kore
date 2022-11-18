package arguments.numbers

import arguments.ExperienceType
import arguments.json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

class Xp(val value: Long, val type: ExperienceType = ExperienceType.POINTS) {
	val typeString get() = json.encodeToJsonElement(type).jsonPrimitive.content
	
	operator fun plus(other: Xp) = Xp(value + other.value, type)
	operator fun minus(other: Xp) = Xp(value - other.value, type)
	operator fun times(other: Xp) = Xp(value * other.value, type)
	operator fun div(other: Xp) = Xp(value / other.value, type)
	operator fun rem(other: Xp) = Xp(value % other.value, type)
	
	fun toLevels() = Xp(value, ExperienceType.LEVELS)
	fun toPoints() = Xp(value, ExperienceType.POINTS)
	
	override fun toString() = when (type) {
		ExperienceType.LEVELS -> "${value}L"
		ExperienceType.POINTS -> value.toString()
	}
}

val Number.levels get() = Xp(toLong(), ExperienceType.LEVELS)
val Number.points get() = Xp(toLong(), ExperienceType.POINTS)
