package commands

import arguments.numbers.TimeType
import arguments.types.literals.int
import arguments.types.literals.literal
import functions.Function
import serializers.LowercaseSerializer
import utils.asArg
import kotlinx.serialization.Serializable

@Serializable(TimePeriod.Companion.TimeSpecSerializer::class)
enum class TimePeriod {
	DAY,
	NOON,
	NIGHT,
	MIDNIGHT;

	companion object {
		data object TimeSpecSerializer : LowercaseSerializer<TimePeriod>(entries)
	}
}

class Time(private val fn: Function) {
	fun add(value: Int) = fn.addLine(command("time", literal("add"), int(value)))
	fun query(type: TimeType) = fn.addLine(command("time", literal("query"), literal(type.asArg())))
	fun set(value: Int) = fn.addLine(command("time", literal("set"), int(value)))
	fun set(period: TimePeriod) = fn.addLine(command("time", literal("set"), literal(period.asArg())))
}

val Function.time get() = Time(this)
fun Function.time(block: Time.() -> Command) = Time(this).block()
