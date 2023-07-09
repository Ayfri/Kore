package commands

import arguments.int
import arguments.literal
import arguments.numbers.TimeType
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg

@Serializable(TimeSpec.Companion.TimeSpecSerializer::class)
enum class TimeSpec {
	DAY,
	NOON,
	NIGHT,
	MIDNIGHT;

	companion object {
		data object TimeSpecSerializer : LowercaseSerializer<TimeSpec>(entries)
	}
}

class Time(private val fn: Function) {
	fun add(value: Int) = fn.addLine(command("time", literal("add"), int(value)))
	fun query(type: TimeType) = fn.addLine(command("time", literal("query"), literal(type.asArg())))
	fun set(value: Int) = fn.addLine(command("time", literal("set"), int(value)))
	fun set(spec: TimeSpec) = fn.addLine(command("time", literal("set"), literal(spec.asArg())))
}

val Function.time get() = Time(this)
fun Function.time(block: Time.() -> Command) = Time(this).block()
