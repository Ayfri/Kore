package arguments.numbers

import arguments.literal
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

@Serializable(TimeType.Companion.TimeTypeSerializer::class)
enum class TimeType(val unit: String) {
	TICKS("t"),
	SECONDS("s"),
	DAYS("d");

	companion object {
		data object TimeTypeSerializer : LowercaseSerializer<TimeType>(entries) {
			override fun serialize(encoder: Encoder, value: TimeType) {
				encoder.encodeString(value.unit)
			}
		}
	}
}

class TimeNumber(val value: Double, val type: TimeType = TimeType.TICKS) : Comparable<TimeNumber> {
	operator fun plus(other: TimeNumber) = TimeNumber(value + other.value, type)
	operator fun minus(other: TimeNumber) = TimeNumber(value - other.value, type)
	operator fun times(other: TimeNumber) = TimeNumber(value * other.value, type)
	operator fun div(other: TimeNumber) = TimeNumber(value / other.value, type)
	operator fun rem(other: TimeNumber) = TimeNumber(value % other.value, type)
	operator fun unaryMinus() = TimeNumber(-value, type)
	operator fun unaryPlus() = TimeNumber(+value, type)
	override operator fun compareTo(other: TimeNumber) = value.compareTo(other.value)

	fun asString() = toString()
	fun asArg() = literal(asString())

	fun inTicks() = when (type) {
		TimeType.TICKS -> TimeNumber(value, TimeType.TICKS)
		TimeType.SECONDS -> TimeNumber(value * 20, TimeType.TICKS)
		TimeType.DAYS -> TimeNumber(value * 24000, TimeType.TICKS)
	}

	fun inSeconds() = when (type) {
		TimeType.TICKS -> TimeNumber(value / 20, TimeType.SECONDS)
		TimeType.SECONDS -> TimeNumber(value, TimeType.SECONDS)
		TimeType.DAYS -> TimeNumber(value * 1200, TimeType.SECONDS)
	}

	fun inDays() = when (type) {
		TimeType.TICKS -> TimeNumber(value / 24000, TimeType.DAYS)
		TimeType.SECONDS -> TimeNumber(value / 1200, TimeType.DAYS)
		TimeType.DAYS -> TimeNumber(value, TimeType.DAYS)
	}

	fun toTicks() = TimeNumber(value, TimeType.TICKS)
	fun toSeconds() = TimeNumber(value, TimeType.SECONDS)
	fun toDays() = TimeNumber(value, TimeType.DAYS)

	override fun toString() = when (type) {
		TimeType.TICKS -> value.str
		TimeType.SECONDS -> "${value.str}s"
		TimeType.DAYS -> "${value.str}d"
	}
}

val Number.ticks get() = TimeNumber(toDouble())
val Number.seconds get() = TimeNumber(toDouble(), TimeType.SECONDS)
val Number.days get() = TimeNumber(toDouble(), TimeType.DAYS)
fun time(value: Number = 0, type: TimeType = TimeType.TICKS) = TimeNumber(value.toDouble(), type)
