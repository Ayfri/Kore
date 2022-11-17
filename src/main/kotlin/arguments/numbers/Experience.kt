package arguments.numbers

import arguments.ExperienceType
import arguments.json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

class Experience(val value: Long, val type: ExperienceType = ExperienceType.POINTS) {
	val typeString get() = json.encodeToJsonElement(type).jsonPrimitive.content
	
	operator fun plus(other: Experience) = Experience(value + other.value, type)
	operator fun minus(other: Experience) = Experience(value - other.value, type)
	operator fun times(other: Experience) = Experience(value * other.value, type)
	operator fun div(other: Experience) = Experience(value / other.value, type)
	operator fun rem(other: Experience) = Experience(value % other.value, type)
	
	fun toLevels() = Experience(value, ExperienceType.LEVELS)
	fun toPoints() = Experience(value, ExperienceType.POINTS)
	
	override fun toString() = when (type) {
		ExperienceType.LEVELS -> "${value}L"
		ExperienceType.POINTS -> value.toString()
	}
}

val Number.levels get() = Experience(toLong(), ExperienceType.LEVELS)
val Number.points get() = Experience(toLong(), ExperienceType.POINTS)
