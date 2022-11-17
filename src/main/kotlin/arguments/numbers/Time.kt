package arguments.numbers

import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

@Serializable(TimeType.Companion.TimeTypeSerializer::class)
enum class TimeType(val unit: String) {
	TICKS("t"),
	SECONDS("s"),
	DAYS("d");
	
	companion object {
		val values = values()
		
		object TimeTypeSerializer : LowercaseSerializer<TimeType>(values) {
			override fun serialize(encoder: Encoder, value: TimeType) {
				encoder.encodeString(value.unit)
			}
		}
	}
}

class TimeNumber(val value: Long, val type: TimeType = TimeType.TICKS) {
	operator fun plus(other: TimeNumber) = TimeNumber(value + other.value, type)
	operator fun minus(other: TimeNumber) = TimeNumber(value - other.value, type)
	operator fun times(other: TimeNumber) = TimeNumber(value * other.value, type)
	operator fun div(other: TimeNumber) = TimeNumber(value / other.value, type)
	operator fun rem(other: TimeNumber) = TimeNumber(value % other.value, type)
	
	fun toTicks() = TimeNumber(value, TimeType.TICKS)
	fun toSeconds() = TimeNumber(value, TimeType.SECONDS)
	fun toDays() = TimeNumber(value, TimeType.DAYS)
	
	override fun toString() = when (type) {
		TimeType.TICKS -> value.toString()
		TimeType.SECONDS -> "${value}s"
		TimeType.DAYS -> "${value}d"
	}
}

val Number.ticks get() = TimeNumber(toLong())
val Number.seconds get() = TimeNumber(toLong(), TimeType.SECONDS)
val Number.days get() = TimeNumber(toLong(), TimeType.DAYS)
fun time(value: Number = 0, type: TimeType = TimeType.TICKS) = TimeNumber(value.toLong(), type)
